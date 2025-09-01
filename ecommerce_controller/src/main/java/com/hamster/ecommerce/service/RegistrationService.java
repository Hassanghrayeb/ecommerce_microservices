package com.hamster.ecommerce.service;

import com.hamster.ecommerce.model.entity.UserRegistration;
import com.hamster.ecommerce.model.simple.UserRegistrationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RegistrationService
{
    UserRegistrationResponse registerUser(UserRegistration userRegistration);
    boolean verifyUser(Long userId);
    Page<UserRegistration> find(Pageable pageable);
}
