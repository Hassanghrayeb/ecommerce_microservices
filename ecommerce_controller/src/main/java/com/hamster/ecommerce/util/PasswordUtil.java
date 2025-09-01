package com.hamster.ecommerce.util;

import org.apache.commons.lang3.StringUtils;

public class PasswordUtil
{
    private static final String lowerAlpha = "abcdefghijklmnopqrstuvwxyz";
    private static final String upperAlpha = lowerAlpha.toUpperCase();
    private static final String digits = "0123456789";
    private static final String specialChars = "!@#$%&*()'+,-./:;<=>?[]^_`{|}";

    public static boolean passwordIsValid(String password)
    {
        if (StringUtils.isBlank(password))
            return false;

        boolean hasLowerAlpha = StringUtils.containsAny(password, lowerAlpha);
        boolean hasUpperAlpha = StringUtils.containsAny(password, upperAlpha);
        boolean hasDigit = StringUtils.containsAny(password, digits);
        boolean hasSpecial = StringUtils.containsAny(password, specialChars);
        boolean isAtLeast8CharsLong = password.length() >= 8;

        return hasLowerAlpha && hasUpperAlpha && hasDigit && hasSpecial && isAtLeast8CharsLong;
    }
}
