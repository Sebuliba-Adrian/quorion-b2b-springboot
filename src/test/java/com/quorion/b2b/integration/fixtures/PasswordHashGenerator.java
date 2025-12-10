package com.quorion.b2b.integration.fixtures;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility to generate and verify BCrypt password hashes for tests
 */
public class PasswordHashGenerator {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Generate a BCrypt hash for the given password
     */
    public static String generateHash(String password) {
        return encoder.encode(password);
    }

    /**
     * Verify a password against a hash
     */
    public static boolean verify(String password, String hash) {
        return encoder.matches(password, hash);
    }

    /**
     * Main method to generate hash for seed data
     */
    public static void main(String[] args) {
        String password = "Test123456!";
        String hash = generateHash(password);
        System.out.println("Password: " + password);
        System.out.println("Hash: " + hash);
        System.out.println("Verification: " + verify(password, hash));
    }
}


