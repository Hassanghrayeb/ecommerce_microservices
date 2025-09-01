package com.hamster.ecommerce.util;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class EncryptionUtil implements PasswordEncoder
{
    private static final Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(
            16, 32, 1, 4096, 3);

    @Override
    public String encode(CharSequence rawPassword)
    {
        return encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword)
    {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
