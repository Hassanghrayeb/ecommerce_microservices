package com.hamster.ecommerce.service.impl;

import com.hamster.ecommerce.exception.ConflictException;
import com.hamster.ecommerce.exception.ErrorCode;
import com.hamster.ecommerce.exception.NotFoundException;
import com.hamster.ecommerce.mapper.RegistrationMapper;
import com.hamster.ecommerce.model.dto.UserRegistrationDTO;
import com.hamster.ecommerce.model.entity.Login;
import com.hamster.ecommerce.model.entity.Person;
import com.hamster.ecommerce.model.entity.Role;
import com.hamster.ecommerce.model.entity.UserRegistration;
import com.hamster.ecommerce.model.simple.UserRegistrationResponse;
import com.hamster.ecommerce.repository.LoginRepository;
import com.hamster.ecommerce.repository.PersonRepository;
import com.hamster.ecommerce.repository.RegistrationRepository;
import com.hamster.ecommerce.service.RegistrationService;
import com.hamster.ecommerce.service.RoleService;
import com.hamster.ecommerce.util.EncryptionUtil;
import com.hamster.ecommerce.util.PasswordUtil;
import com.sun.jdi.InternalException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.hamster.ecommerce.model.enums.RoleEnum.STANDARD;

@Service
public class RegistrationServiceImpl implements RegistrationService
{
    private final RegistrationRepository registrationRepository;
    private final EncryptionUtil encryptionUtil;
    private final LoginRepository loginRepository;
    private final RoleService roleService;
    private final PersonRepository personRepository;
    private final RegistrationMapper registrationMapper;

    public RegistrationServiceImpl(RegistrationRepository registrationRepository,
            EncryptionUtil encryptionUtil, LoginRepository loginRepository,
            RoleService roleService, PersonRepository personRepository, RegistrationMapper registrationMapper)
    {
        this.registrationRepository = registrationRepository;
        this.encryptionUtil = encryptionUtil;
        this.loginRepository = loginRepository;
        this.roleService = roleService;
        this.personRepository = personRepository;
        this.registrationMapper = registrationMapper;
    }

    @Override
    public Page<UserRegistration> find(Pageable pageable)
    {
        return (Page<UserRegistration>) registrationRepository.findByStatus("PENDING",pageable);
    }

    @Override
    @Transactional
    public UserRegistrationResponse registerUser(UserRegistration userRegistration)
    {
        applyUserInputValidationRules(userRegistration);
        validateUserNotExists(userRegistration.getEmailAddress());

        UserRegistration existingUserRegistration = registrationRepository.findByEmail(userRegistration.getEmailAddress()).orElse(null);

        UserRegistrationResponse userRegistrationResponse = new UserRegistrationResponse();
        userRegistrationResponse.setUserEmailAlreadyRequested(false);

        if (Objects.nonNull(existingUserRegistration))
        {
            userRegistrationResponse.setUserEmailAlreadyRequested(true);
            Long userId = existingUserRegistration.getId();
            userRegistration.setId(userId);
        }

        setPasswordIfChangedOrMissing(existingUserRegistration, userRegistration);

        userRegistration.setEmailAddress(StringUtils.lowerCase(userRegistration.getEmailAddress()));
        userRegistration.setStatus("PENDING");
        existingUserRegistration = registrationRepository.save(userRegistration);

        UserRegistrationDTO userRegistrationDTO = registrationMapper.entityToDTO(existingUserRegistration);
        userRegistrationResponse.setUserRegistrationDTO(userRegistrationDTO);

        return userRegistrationResponse;
    }

    @Override
    @Transactional("postgresTransactionManager")
    public boolean verifyUser(Long userId)
    {
        UserRegistration userRegistered = registrationRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Registered User not found with id" + userId));
        userRegistered.setStatus("VERIFIED");
        userRegistered = registrationRepository.save(userRegistered);

        Login newLogin = saveRegisteredUserAsNewLogin(userRegistered);
        saveRegisteredUserAsNewPerson(userRegistered, newLogin.getId());

        return true;
    }

    private void applyUserInputValidationRules(UserRegistration userRegistration)
    {
        if (!PasswordUtil.passwordIsValid(userRegistration.getPassword()))
        {
            throw new ConflictException(ErrorCode.PASSWORD_TOO_WEAK);
        }

        if (!userRegistration.getPassword().equals(userRegistration.getConfirmPassword()))
        {
            throw new ConflictException(ErrorCode.PASSWORDS_DO_NOT_MATCH);
        }
    }

    @Transactional("postgresTransactionManager")
    private Login saveRegisteredUserAsNewLogin(UserRegistration userRegistered)
    {
        Role freeUserRole = roleService.findByName(STANDARD.toString()).orElseThrow(() -> new InternalException("Something went wrong"));

        Login login = new Login();
        login.setUsername(StringUtils.lowerCase(userRegistered.getEmailAddress()));
        login.setUpdateTimestamp(LocalDateTime.now());
        login.setEnabled(true);
        login.setPassword(userRegistered.getPassword());
        login.setUpdateLoginId(0L);
        login.setRoles(List.of(freeUserRole));
        login.setUpdateTimestamp(LocalDateTime.now());

        Login savedLogin = loginRepository.save(login);

        loginRepository.deleteUserRolesByLoginId(savedLogin.getId());
        loginRepository.insertUserRoleRecord(savedLogin.getId(), freeUserRole.getId(), savedLogin.getId(), LocalDateTime.now());

        return savedLogin;
    }

    @Transactional("postgresTransactionManager")
    private void saveRegisteredUserAsNewPerson(UserRegistration userRegistered, Long loginId)
    {
        Person personToSave = new Person();
        personToSave.setLoginId(loginId);
        personToSave.setStatusId(1L);
        personToSave.setFirstName(userRegistered.getFirstName());
        personToSave.setLastName(userRegistered.getLastName());
        personToSave.setEmail(StringUtils.lowerCase(userRegistered.getEmailAddress()));

        personRepository.save(personToSave);
    }

    private void setPasswordIfChangedOrMissing(UserRegistration existingUser, UserRegistration updatedUser)
    {
        if (Objects.isNull(existingUser) || !encryptionUtil.matches(updatedUser.getPassword(), existingUser.getPassword()))
        {
            updatedUser.setPassword(encryptionUtil.encode(updatedUser.getPassword()));
        }
        else
            updatedUser.setPassword(existingUser.getPassword());
    }

    private void validateUserNotExists(String email)
    {
        if (loginRepository.doesUsernameExist(email))
        {
            throw new ConflictException(ErrorCode.USERNAME_EXISTS);
        }
    }
}
