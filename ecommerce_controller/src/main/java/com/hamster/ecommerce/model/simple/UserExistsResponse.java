package com.hamster.ecommerce.model.simple;

public class UserExistsResponse
{
    public Boolean exists;

    public UserExistsResponse(Boolean exists)
    {
        this.exists = exists;
    }

    public Boolean getExists()
    {
        return exists;
    }

    public void setExists(Boolean exists)
    {
        this.exists = exists;
    }
}
