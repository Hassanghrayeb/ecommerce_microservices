package com.hamster.ecommerce.model.simple;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class AuthResponse
{
    private final Map<String, Object> responseMap = new HashMap<>();
    private HttpStatus httpStatus;

    public Map<String, Object> getResponseMap()
    {
        return responseMap;
    }

    public HttpStatus getHttpStatus()
    {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus)
    {
        this.httpStatus = httpStatus;
    }
}
