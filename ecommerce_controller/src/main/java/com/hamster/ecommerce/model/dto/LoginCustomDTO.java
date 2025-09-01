package com.hamster.ecommerce.model.dto;

public class LoginCustomDTO
{
    private Long id;
    private String username;
    private String password;
    private Boolean enabled;
    private Long[] roles;

    private PersonDTO person;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

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

    public Boolean getEnabled()
    {
        return enabled;
    }

    public void setEnabled(Boolean enabled)
    {
        this.enabled = enabled;
    }

    public Long[] getRoles()
    {
        return roles;
    }

    public void setRoles(Long[] roles)
    {
        this.roles = roles;
    }

    public PersonDTO getPerson()
    {
        return person;
    }

    public void setPerson(PersonDTO personDTO)
    {
        this.person = personDTO;
    }
}
