package com.hamster.ecommerce.controller;


import com.hamster.ecommerce.model.simple.AuthRequest;
import com.hamster.ecommerce.model.simple.AuthResponse;
import com.hamster.ecommerce.model.simple.RefreshRequest;
import com.hamster.ecommerce.model.simple.UserExistsRequest;
import com.hamster.ecommerce.model.simple.UserExistsResponse;
import com.hamster.ecommerce.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Auth Controller", description = "Auth Operations")
@RestController
@RequestMapping(path = "/auth")
public class AuthController
{
    private final AuthService authService;

    public AuthController(AuthService authService)
    {
        this.authService = authService;
    }

    @Operation(summary = "Authenticate with principal and credential")
    @PostMapping(path = "")
    ResponseEntity<Map<String, Object>> authenticate(@Valid @RequestBody AuthRequest authRequest)
    {
        AuthResponse authResponse = authService.validateUser(authRequest);
        return new ResponseEntity<>(authResponse.getResponseMap(), authResponse.getHttpStatus());
    }

    @Operation(summary = "Request a refresh token")
    @PostMapping(path = "/refresh")
    ResponseEntity<Map<String, Object>> refresh(@Valid @RequestBody RefreshRequest refreshRequest)
    {
        AuthResponse authResponse = authService.refreshUserToken(refreshRequest);
        return new ResponseEntity<>(authResponse.getResponseMap(), authResponse.getHttpStatus());
    }

    @Operation(summary = "Verify if a user exists")
    @PostMapping("/user-exists")
    public ResponseEntity<UserExistsResponse> verifyUserExists(@Valid @RequestBody UserExistsRequest otpVerificationRequest)
    {
        return new ResponseEntity<>(authService.userExists(otpVerificationRequest.getEmailAddress()), HttpStatus.OK);
    }
}
