package com.hamster.ecommerce.model.simple;

import com.hamster.ecommerce.model.dto.UserRegistrationDTO;

public class UserRegistrationResponse
{
    private UserRegistrationDTO userRegistrationDTO;
    private boolean userEmailAlreadyRequested;

    public UserRegistrationDTO getUserRegistrationDTO()
    {
        return userRegistrationDTO;
    }

    public void setUserRegistrationDTO(UserRegistrationDTO userRegistrationDTO)
    {
        this.userRegistrationDTO = userRegistrationDTO;
    }

    public boolean isUserEmailAlreadyRequested()
    {
        return userEmailAlreadyRequested;
    }

    public void setUserEmailAlreadyRequested(boolean userEmailAlreadyRequested)
    {
        this.userEmailAlreadyRequested = userEmailAlreadyRequested;
    }
}
