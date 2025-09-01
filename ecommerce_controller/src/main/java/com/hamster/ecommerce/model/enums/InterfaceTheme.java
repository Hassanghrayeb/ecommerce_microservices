package com.hamster.ecommerce.model.enums;

public enum InterfaceTheme
{
    LIGHT,
    DARK;

    public static InterfaceTheme fromString(String theme)
    {
        try
        {
            return InterfaceTheme.valueOf(theme.toUpperCase());
        }
        catch (IllegalArgumentException e)
        {
            throw new IllegalArgumentException("Invalid theme: " + theme);
        }
    }
}
