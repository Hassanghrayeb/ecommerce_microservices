package com.hamster.ecommerce.exception;

import java.io.Serial;

public class NotFoundException extends RuntimeException
{
    @Serial
    private static final long serialVersionUID = 4_954_644_341_942_908_988L;

    public NotFoundException(Long id)
    {
        super("Item not found : " + id);
    }

    public NotFoundException(Long id, String message)
    {
        super("Item not found : " + id + " - " + message);
    }

    public NotFoundException(String key)
    {
        super("Item not found : " + key);
    }
}
