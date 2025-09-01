package com.hamster.ecommerce.model.simple;

import jakarta.validation.constraints.NotEmpty;

public class UserExistsRequest
{
    @NotEmpty(message = "The email address is required")
    public String emailAddress;

    public String getEmailAddress()
    {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
    }
}
