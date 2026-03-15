package ru.kubsu.borshchevyk.auth.domain.model.result;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * Result of a login request, providing the user's cryptographic keys.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 * @param userId the unique identifier of the logged-in user
 * @param publicKey the user's public key
 * @param encryptedPrivateKey the user's private key, encrypted for transport
 */
@Slf4j
@Builder
public record LoginResult(
        UUID userId,
        String publicKey,
        String encryptedPrivateKey
) {
    /**
     * Compact constructor for {@code LoginResult} that performs minimal logging.
     */
    public LoginResult {
        log.debug("Created LoginResult for user: {}", userId);
    }
}
