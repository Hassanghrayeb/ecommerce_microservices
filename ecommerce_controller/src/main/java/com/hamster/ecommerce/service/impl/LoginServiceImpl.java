package com.hamster.ecommerce.service.impl;

import com.hamster.ecommerce.config.AppProperties;
import com.hamster.ecommerce.exception.NotFoundException;
import com.hamster.ecommerce.model.dto.SimpleIdStatusDTO;
import com.hamster.ecommerce.model.entity.Login;
import com.hamster.ecommerce.model.entity.Person;
import com.hamster.ecommerce.model.entity.Role;
import com.hamster.ecommerce.repository.LoginRepository;
import com.hamster.ecommerce.repository.PermissionRepository;
import com.hamster.ecommerce.repository.PersonRepository;
import com.hamster.ecommerce.repository.RoleRepository;
import com.hamster.ecommerce.service.LoginService;
import com.hamster.ecommerce.util.ContextUtil;
import com.hamster.ecommerce.util.EncryptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LoginServiceImpl implements LoginService
{
    private static final Logger log = LoggerFactory.getLogger(LoginServiceImpl.class);
    /*------------------------------------------------------------------------------+
    |    A password is valid if...                                                  |
    |    It contains at least 8 characters                                          |
    |    It contains at least one digit.                                            |
    |    It contains at least one upper case alphabet.                              |
    |    It contains at least one lower case alphabet.                              |
    |    It contains at least one special character which includes !@#$%&*()-+=^.   |
    |    It doesn't contain any white space.                                        |
    +------------------------------------------------------------------------------*/
    private static final String validPasswordRegEx = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[:punct:])(?=\\S+$).{8,}$";

    private final PersonRepository personRepository;
    private final LoginRepository loginRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final ContextUtil contextUtil;
    private final EncryptionUtil encryptionUtil;
    private final AppProperties appProperties;

    public LoginServiceImpl(LoginRepository loginRepository, RoleRepository roleRepository,
            PersonRepository personRepository, PermissionRepository permissionRepository,
            ContextUtil contextUtil, EncryptionUtil encryptionUtil, AppProperties appProperties)
    {
        this.loginRepository = loginRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.contextUtil = contextUtil;
        this.encryptionUtil = encryptionUtil;
        this.personRepository = personRepository;
        this.appProperties = appProperties;
    }

    public Page<Login> find(Pageable pageable)
    {
        Page<Login> userPage = loginRepository.findAll(pageable);
        userPage.iterator().forEachRemaining(this::populateLogin);
        return userPage;
    }

    @Override
    public Optional<Login> findByUsername(String username)
    {
        Optional<Login> optionalUser = loginRepository.findByUsername(username);
        return optionalUser.map(this::populateLogin);
    }

    @Override
    public Optional<Login> findById(Long id)
    {
        Optional<Login> optionalUser = loginRepository.findById(id);
        return optionalUser.map(this::populateLogin);
    }

    private Login populateLogin(Login login)
    {
        List<Role> roleList = roleRepository.findByLoginId(login.getId());
        roleList.forEach(role -> role.setPermissions(permissionRepository.findByRoleId(role.getId())));
        login.setRoles(roleList);

        Person person = personRepository.findByLoginId(login.getId()).orElse(null);
        login.setPerson(person);

        return login;
    }

    @Override
    public Page<Login> getUsersWithRole(Pageable pageable, String roleName)
    {
        int limit = pageable.getPageSize();
        int offset = pageable.getPageNumber() * limit;
        List<Login> loginList = loginRepository.getUsersWithRole(roleName, limit, offset);

        return new PageImpl<>(loginList, pageable, loginList.size());
    }

    @Override
    @Transactional("postgresTransactionManager")
    public Login save(Login login, boolean passwordIsPlainText)
    {
        /*------------------------------------------------------+
        |   Whenever we save a user, if a plain text password   |
        |   is being passed in, we need to generate a new salt  |
        |   and rehash the pw to a new digest.                  |
        +------------------------------------------------------*/
        if (passwordIsPlainText)
        {
            login.setPassword(encryptionUtil.encode(login.getPassword()));
        }

        if (login.getEnabled() == null)
            login.setEnabled(false);

        login.setUpdateLoginId(contextUtil.getCurrentUserId());
        login.setUpdateTimestamp(LocalDateTime.now());
        login = loginRepository.save(login);

        return login;
    }

    @Override
    public Boolean doesUsernameExist(String username)
    {
        username = StringUtils.lowerCase(username);
        return loginRepository.doesUsernameExist(username);
    }

    @Override
    @Transactional("postgresTransactionManager")
    public void setLoginRoles(Long loginId, List<Long> roleIdList)
    {
        loginRepository.deleteUserRolesByLoginId(loginId);
        for (Long roleId : roleIdList)
        {
            if (!loginRepository.doesLoginRoleExist(loginId, roleId))
                loginRepository.insertUserRoleRecord(loginId, roleId, contextUtil.getCurrentUserId(), LocalDateTime.now());
        }
    }

    @Override
    public void updateStatus(SimpleIdStatusDTO dto)
    {
        Login login = loginRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException(dto.getId()));

        login.setEnabled(dto.getStatus());
        loginRepository.save(login);
    }

}
