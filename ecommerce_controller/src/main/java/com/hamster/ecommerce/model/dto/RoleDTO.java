package com.hamster.ecommerce.model.dto;


import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

public class RoleDTO
{
    private Long id;
    @NotEmpty(message = "The name is required")
    private String name;
    private String description;
    private List<PermissionDTO> permissions = new ArrayList<>();

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public List<PermissionDTO> getPermissions()
    {
        return permissions;
    }

    public void setPermissions(List<PermissionDTO> permissions)
    {
        this.permissions = permissions;
    }
}
