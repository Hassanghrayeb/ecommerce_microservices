package com.hamster.ecommerce.service.impl;

import com.hamster.ecommerce.model.dto.UserValidationDTO;
import com.hamster.ecommerce.model.entity.Login;
import com.hamster.ecommerce.model.entity.Person;
import com.hamster.ecommerce.service.UserValidationService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserValidationServiceImpl implements UserValidationService
{
    public UserValidationServiceImpl() {}

    @Override
    public UserValidationDTO validateUser(Login login)
    {
        UserValidationDTO userValidationDTO = new UserValidationDTO();
        if (login == null)
        {
            userValidationDTO.setMessage("Login object is null");
            return userValidationDTO;
        }

        Person person = login.getPerson();
        if (person == null)
        {
            userValidationDTO.setMessage("Person object is null");
            userValidationDTO.addRequiredField("firstName");
            userValidationDTO.addRequiredField("lastName");
            userValidationDTO.addRequiredField("email");
            return userValidationDTO;
        }
        if (!StringUtils.hasText(person.getFirstName()))
        {
            userValidationDTO.addRequiredField("firstName");
        }
        if (!StringUtils.hasText(person.getLastName()))
        {
            userValidationDTO.addRequiredField("lastName");
        }
        if (!StringUtils.hasText(person.getEmail()))
        {
            userValidationDTO.addRequiredField("email");
        }
        return userValidationDTO;
    }
}
