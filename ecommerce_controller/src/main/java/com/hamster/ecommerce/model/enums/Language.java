package com.hamster.ecommerce.model.enums;

public enum Language
{
    ENGLISH,
    FRENCH,
    ARABIC;

    public static Language fromString(String theme)
    {
        try
        {
            return Language.valueOf(theme.toUpperCase());
        }
        catch (IllegalArgumentException e)
        {
            throw new IllegalArgumentException("Invalid theme: " + theme);
        }
    }
}
