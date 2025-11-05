package com.quorion.b2b.domain.port.output.auth;

/**
 * Output Port for Password Operations
 * Provides password verification functionality
 */
public interface PasswordPort {

    /**
     * Verify if the provided password matches the stored password hash
     * @param rawPassword The raw password to verify
     * @param encodedPassword The encoded password hash to compare against
     * @return true if password matches, false otherwise
     */
    boolean verifyPassword(String rawPassword, String encodedPassword);

    /**
     * Encode a raw password
     * @param rawPassword The raw password to encode
     * @return The encoded password hash
     */
    String encodePassword(String rawPassword);
}


