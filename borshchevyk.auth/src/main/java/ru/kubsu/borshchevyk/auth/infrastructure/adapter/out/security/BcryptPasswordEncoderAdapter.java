package ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.auth.application.port.out.PasswordEncoderPort;

/**
 * Adapter for password encoding using BCrypt.
 */
@Slf4j
@Component
public class BcryptPasswordEncoderAdapter implements PasswordEncoderPort {

    private final PasswordEncoder passwordEncoder;

    /**
     * Default constructor initializing BCryptPasswordEncoder.
     */
    public BcryptPasswordEncoderAdapter() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Checks if a raw password matches an encoded password.
     *
     * @param rawPassword     the raw password
     * @param encodedPassword the encoded password
     * @return true if the passwords match, false otherwise
     */
    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        log.debug("Checking password match");
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * Encodes a raw password.
     *
     * @param rawPassword the raw password to encode
     * @return the encoded password
     */
    @Override
    public String encode(String rawPassword) {
        log.debug("Encoding raw password");
        return passwordEncoder.encode(rawPassword);
    }
}
