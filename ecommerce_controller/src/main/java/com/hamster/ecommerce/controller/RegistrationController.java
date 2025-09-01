package com.hamster.ecommerce.controller;

import com.hamster.ecommerce.mapper.RegistrationMapper;
import com.hamster.ecommerce.model.dto.UserRegistrationDTO;
import com.hamster.ecommerce.model.simple.UserRegistrationResponse;
import com.hamster.ecommerce.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Registration Controller")
@RestController
@RequestMapping("/registration")
public class RegistrationController
{
    private final RegistrationService registrationService;
    private final RegistrationMapper registrationMapper;

    public RegistrationController(RegistrationService registrationService, RegistrationMapper registrationMapper)
    {
        this.registrationService = registrationService;
        this.registrationMapper = registrationMapper;
    }

    @Operation(summary = "Register a User")
    @PostMapping("")
    public ResponseEntity<UserRegistrationResponse> registerUser(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO)
    {

        UserRegistrationResponse userRegistrationResponse = registrationService
                .registerUser(registrationMapper.dtoToEntity(userRegistrationDTO));

        return new ResponseEntity<>(userRegistrationResponse, HttpStatus.OK);
    }

}
