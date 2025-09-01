package com.hamster.ecommerce.model.simple;

import jakarta.validation.constraints.NotEmpty;


public class AuthRequest
{
    @NotEmpty(message = "The username is required")
    private String username;

    @NotEmpty(message = "The password is required")
    private String password;

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
