package com.hamster.ecommerce.controller;

import com.hamster.ecommerce.mapper.RegistrationMapper;
import com.hamster.ecommerce.model.dto.UserRegistrationDTO;
import com.hamster.ecommerce.model.entity.UserRegistration;
import com.hamster.ecommerce.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin Registration Controller")
@RestController
@RequestMapping("/admin/registration")
public class AdminRegistrationController
{
    private final RegistrationService registrationService;
    private final RegistrationMapper registrationMapper;

    public AdminRegistrationController(RegistrationService registrationService, RegistrationMapper registrationMapper)
    {
        this.registrationService = registrationService;
        this.registrationMapper = registrationMapper;
    }

    @Operation(summary = "Get all Registered Users, in paged format")
    @GetMapping("")
    ResponseEntity<Page<UserRegistrationDTO>> findAllPaged(@PageableDefault(page = 0, size = 10) Pageable pageable)
    {
        Page<UserRegistration> cartPage = registrationService.find(pageable);
        return new ResponseEntity<>(cartPage.map(registrationMapper::entityToDTO), HttpStatus.OK);
    }

    @Operation(summary = "Verify a Registered User")
    @PostMapping("/user/{id}/verify")
    public ResponseEntity<Object> verifyUser(@PathVariable Long id)
    {
        registrationService.verifyUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
