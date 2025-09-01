package com.hamster.ecommerce.model.dto;

import jakarta.validation.constraints.NotNull;

public class SimpleIdStatusDTO
{

    @NotNull
    private Long id;
    @NotNull
    private Boolean status;

    public SimpleIdStatusDTO()
    {
    }

    public SimpleIdStatusDTO(Long id, Boolean status)
    {
        this.id = id;
        this.status = status;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Boolean getStatus()
    {
        return status;
    }

    public void setStatus(Boolean status)
    {
        this.status = status;
    }
}
