package com.hamster.ecommerce.service;

import com.hamster.ecommerce.exception.ErrorCode;
import com.hamster.ecommerce.model.simple.AuthRequest;
import com.hamster.ecommerce.model.simple.AuthResponse;
import com.hamster.ecommerce.model.simple.RefreshRequest;
import com.hamster.ecommerce.model.simple.UserExistsResponse;

public interface AuthService
{
    AuthResponse validateUser(AuthRequest authRequest);

    AuthResponse refreshUserToken(RefreshRequest refreshRequest);

    void logUserOut();

    ErrorCode deleteAccount();

    UserExistsResponse userExists(String emailAddress);
}
