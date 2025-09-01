package com.hamster.ecommerce.service;

import com.hamster.ecommerce.model.dto.UserValidationDTO;
import com.hamster.ecommerce.model.entity.Login;

public interface UserValidationService
{
    UserValidationDTO validateUser(Login login);
}
