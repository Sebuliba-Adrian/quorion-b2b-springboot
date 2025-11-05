package com.quorion.b2b.adapter.output.security;

import com.quorion.b2b.domain.port.output.auth.PasswordPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Password Adapter - Implements PasswordPort using Spring Security's PasswordEncoder
 */
@Component
public class PasswordAdapter implements PasswordPort {

    private final PasswordEncoder passwordEncoder;

    public PasswordAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        try {
            return passwordEncoder.matches(rawPassword, encodedPassword);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}

