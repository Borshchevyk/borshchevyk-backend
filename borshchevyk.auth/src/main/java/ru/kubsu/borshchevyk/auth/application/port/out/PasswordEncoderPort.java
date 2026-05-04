package ru.kubsu.borshchevyk.auth.application.port.out;

/**
 * Port for encoding passwords.
 */
public interface PasswordEncoderPort {
    /**
     * Encodes a raw password.
     *
     * @param rawPassword the raw password string
     * @return the encoded password string
     */
    String encode(String rawPassword);

    /**
     * Matches a raw password against an encoded password.
     *
     * @param rawPassword     the raw password string
     * @param encodedPassword the encoded password string to match against
     * @return true if the passwords match, false otherwise
     */
    boolean matches(String rawPassword, String encodedPassword);
}
