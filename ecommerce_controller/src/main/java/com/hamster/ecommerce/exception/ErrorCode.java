package com.hamster.ecommerce.exception;

public enum ErrorCode
{
    NO_ERROR("no_error", "No Error"),
    BAD_REQUEST("bad_request", "Bad Request"),
    MISSING_DATA("missing_data", "Missing Data"),
    INVALID_DATA("invalid_data", "Invalid Data"),
    CONFLICT("conflict", "Data Conflict"),
    INVALID_USER("invalid_user", "Invalid User"),
    PASSWORDS_DO_NOT_MATCH("passwords_do_not_match", "Passwords don't match"),
    USERNAME_EXISTS("username_exists", "Username already exists"),
    INVALID_ROLES("invalid_roles", "One or more roles entered do not exist"),
    PASSWORD_TOO_WEAK("password_too_weak", "Must be min 8, contain one upper, lower, number and special char.  No whitespace."),
    SYSTEM_USER("system_user", "You may not delete a system User.");

    private final String code;
    private final String message;
    private final String info;

    ErrorCode(String code, String message)
    {
        this.code = code;
        this.message = message;
        this.info = "";
    }

    ErrorCode(String code, String message, String info)
    {
        this.code = code;
        this.message = message;
        this.info = info;
    }

    public String getCode()
    {
        return code;
    }

    public String getMessage()
    {
        return message;
    }

    public String getInfo()
    {
        return info;
    }
}
