package com.hamster.ecommerce.model.simple;


import jakarta.validation.constraints.NotEmpty;

public class RefreshRequest
{
    @NotEmpty(message = "The username is required")
    private String username;

    @NotEmpty(message = "The refresh token is required")
    private String refreshToken;

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getRefreshToken()
    {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken)
    {
        this.refreshToken = refreshToken;
    }
}
