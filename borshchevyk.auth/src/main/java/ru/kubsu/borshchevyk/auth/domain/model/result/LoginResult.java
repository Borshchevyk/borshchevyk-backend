package ru.kubsu.borshchevyk.auth.domain.model.result;

import lombok.Builder;

import java.util.UUID;

/**
 * Result of a login request, providing the user's cryptographic keys.
 *
 * @param userId              the unique identifier of the logged-in user
 * @param publicKey           the user's public key
 * @param encryptedPrivateKey the user's private key, encrypted for transport
 */
@Builder
public record LoginResult(
        UUID userId,
        String publicKey,
        String encryptedPrivateKey
) { }