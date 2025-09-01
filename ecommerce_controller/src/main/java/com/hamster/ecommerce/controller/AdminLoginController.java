package com.hamster.ecommerce.controller;

import com.hamster.ecommerce.exception.ErrorCode;
import com.hamster.ecommerce.exception.NotFoundException;
import com.hamster.ecommerce.mapper.LoginMapper;
import com.hamster.ecommerce.model.dto.LoginCustomDTO;
import com.hamster.ecommerce.model.dto.LoginDTO;
import com.hamster.ecommerce.model.dto.SimpleIdStatusDTO;
import com.hamster.ecommerce.model.entity.Login;
import com.hamster.ecommerce.model.simple.ResponseMessage;
import com.hamster.ecommerce.service.LoginService;
import com.hamster.ecommerce.service.RoleService;
import com.hamster.ecommerce.util.PasswordUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Tag(name = "Admin Login Controller", description = "Operations pertaining to users")
@RestController
@RequestMapping("/admin/login")
public class AdminLoginController
{

    private final LoginMapper loginMapper;
    private final LoginService loginService;
    private final RoleService roleService;

    public AdminLoginController(LoginMapper loginMapper, LoginService loginService, RoleService roleService)
    {
        this.loginMapper = loginMapper;
        this.loginService = loginService;
        this.roleService = roleService;
    }

    @Operation(summary = "Get all users, in paged format")
    @GetMapping()
    ResponseEntity<Page<LoginDTO>> findAllPaged(@PageableDefault(page = 0, size = 10) Pageable pageable)
    {
        Page<Login> loginPage = loginService.find(pageable);
        return new ResponseEntity<>(loginPage.map(loginMapper::entityToDTO), HttpStatus.OK);
    }

    @Operation(summary = "Find all users with a given role, in paged format")
    @GetMapping("/rolename/{roleName}")
    ResponseEntity<Page<LoginDTO>> findAllWithRolename(
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            @PathVariable String roleName)
    {
        Page<Login> userPage = loginService.getUsersWithRole(pageable, roleName);
        return new ResponseEntity<>(userPage.map(loginMapper::entityToDTO), HttpStatus.OK);
    }

    @Operation(summary = "Find a single user, by id")
    @GetMapping("/{id}")
    ResponseEntity<LoginDTO> findOne(@PathVariable Long id)
    {
        Login user = loginService.findById(id).orElseThrow(() -> new NotFoundException(id));
        return new ResponseEntity<>(loginMapper.entityToDTO(user), HttpStatus.OK);
    }

    @Operation(summary = "Save a new user")
    @PostMapping("")
    ResponseEntity<Object> saveLogin(@Valid @RequestBody LoginCustomDTO dto)
    {
        //A new user already requires a password change
        dto.setId(null);

        //Verify password strength is sufficient
        if (!PasswordUtil.passwordIsValid(dto.getPassword()))
        {
            return new ResponseEntity<>(new ResponseMessage(ErrorCode.PASSWORD_TOO_WEAK), HttpStatus.BAD_REQUEST);
        }

        //if this username is already in use, return a bad request
        if (Boolean.TRUE.equals(loginService.doesUsernameExist(dto.getUsername())))
        {
            return new ResponseEntity<>(new ResponseMessage(ErrorCode.USERNAME_EXISTS), HttpStatus.BAD_REQUEST);
        }

        if (Boolean.FALSE.equals(roleService.doRolesExist(dto.getRoles())))
        {
            return new ResponseEntity<>(new ResponseMessage(ErrorCode.INVALID_ROLES), HttpStatus.BAD_REQUEST);
        }

        HttpStatus saveLoginStatus = dto.getId() == null ? HttpStatus.CREATED : HttpStatus.OK;

        Login user = loginService.save(loginMapper.customDtoToEntity(dto), true);
        loginService.setLoginRoles(user.getId(), Arrays.asList(dto.getRoles()));

        user = loginService.findById(user.getId()).orElse(null);
        return new ResponseEntity<>(loginMapper.entityToDTO(user), saveLoginStatus);
    }

    @Operation(summary = "Update an existing user")
    @PutMapping("/{id}")
    ResponseEntity<Object> updateLogin(@PathVariable Long id, @Valid @RequestBody LoginCustomDTO dto)
    {
        Optional<Login> optionalLogin = loginService.findById(id);
        if (optionalLogin.isEmpty())
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Login user = optionalLogin.get();

        if (Objects.nonNull(dto.getUsername()) &&
                !dto.getUsername().equals(user.getUsername()) &&
                Boolean.TRUE.equals(loginService.doesUsernameExist(dto.getUsername())))
        {
            return new ResponseEntity<>(new ResponseMessage(ErrorCode.USERNAME_EXISTS), HttpStatus.BAD_REQUEST);
        }

        dto.setId(id);
        loginMapper.customDtoToEntity(dto, user);
        /*------------------------------------------------------------------+
        |   If the client passed in a new password, it will be plainText.   |
        |   We need to ensure it meets minimum password strength.           |
        |   We also need to tell the service to encrypt it.  Otherwise it   |
        |   will be the previously encrypted pw and doesn't need encryption |
        +------------------------------------------------------------------*/
        if (Objects.nonNull(dto.getPassword()) && !PasswordUtil.passwordIsValid(dto.getPassword()))
        {
            return new ResponseEntity<>(new ResponseMessage(ErrorCode.PASSWORD_TOO_WEAK), HttpStatus.BAD_REQUEST);
        }

        if (Objects.nonNull(dto.getRoles()) && Boolean.FALSE.equals(roleService.doRolesExist(dto.getRoles())))
        {
            return new ResponseEntity<>(new ResponseMessage(ErrorCode.INVALID_ROLES), HttpStatus.BAD_REQUEST);
        }

        user = loginService.save(user, StringUtils.isNotBlank(dto.getPassword()));
        boolean rolesExist = Objects.nonNull(dto.getRoles());

        if (rolesExist)
            loginService.setLoginRoles(user.getId(), Arrays.asList(dto.getRoles()));

        user = rolesExist ? loginService.findById(id).orElse(null) : user;

        return new ResponseEntity<>(loginMapper.entityToDTO(user), HttpStatus.OK);
    }

    @Operation(summary = "Set the roles for a given user, by user id and an array of role ids")
    @PutMapping("/{userId}/roles/{roleIdList}")
    ResponseEntity<Object> setLoginRoles(@PathVariable Long userId, @PathVariable Long[] roleIdList)
    {
        Optional<Login> optionalLogin = loginService.findById(userId);
        if (optionalLogin.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (!roleService.doRolesExist(roleIdList))
            return new ResponseEntity<>(new ResponseMessage(ErrorCode.INVALID_ROLES), HttpStatus.BAD_REQUEST);

        loginService.setLoginRoles(userId, Arrays.asList(roleIdList));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Update user status")
    @PutMapping("/enabled")
    ResponseEntity<Void> updateLoginStatus(@RequestBody SimpleIdStatusDTO dto)
    {
        loginService.updateStatus(dto);
        return ResponseEntity.ok().build();
    }

}
