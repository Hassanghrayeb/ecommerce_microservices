package com.hamster.ecommerce.model.dto;

import jakarta.validation.constraints.NotBlank;

public class UserRegistrationDTO
{
    private Long id;
    @NotBlank(message = "FirstName cannot be null or empty")
    private String firstName;
    @NotBlank(message = "LastName cannot be null or empty")
    private String lastName;
    @NotBlank(message = "EmailAddress cannot be null or empty")
    private String emailAddress;
    @NotBlank(message = "Password cannot be null or empty")
    private String password;
    @NotBlank(message = "ConfirmPassword cannot be null or empty")
    private String confirmPassword;
    private String status;

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getEmailAddress()
    {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getConfirmPassword()
    {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword)
    {
        this.confirmPassword = confirmPassword;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
}
