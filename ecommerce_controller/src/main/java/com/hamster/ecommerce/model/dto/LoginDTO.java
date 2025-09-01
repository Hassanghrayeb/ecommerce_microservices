package com.hamster.ecommerce.model.dto;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LoginDTO
{
    private Long id;
    private String username;
    private String password;
    private Boolean enabled;
    private LocalDateTime updateTimestamp;

    private List<RoleDTO> roles = new ArrayList<>();
    private PersonDTO personDTO;

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

    public List<RoleDTO> getRoles()
    {
        return roles;
    }

    public void setRoles(List<RoleDTO> roles)
    {
        this.roles = roles;
    }

    public PersonDTO getPerson()
    {
        return personDTO;
    }

    public void setPerson(PersonDTO personDTO)
    {
        this.personDTO = personDTO;
    }

    public LocalDateTime getUpdateTimestamp()
    {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(LocalDateTime updateTimestamp)
    {
        this.updateTimestamp = updateTimestamp;
    }
}
