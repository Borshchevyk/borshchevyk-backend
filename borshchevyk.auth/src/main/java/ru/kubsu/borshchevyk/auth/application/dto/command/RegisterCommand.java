package ru.kubsu.borshchevyk.auth.application.dto.command;

import lombok.Builder;

/**
 * Command for user registration.
 *
 * @param email               user's email address
 * @param tag                 user's unique tag
 * @param firstName           user's first name
 * @param lastName            user's last name
 * @param passwordHash        client-side password hash
 * @param publicKey           user's public key
 * @param encryptedPrivateKey user's encrypted private key
 */
@Builder
public record RegisterCommand(
        String email,
        String tag,
        String firstName,
        String lastName,
        String passwordHash,
        String publicKey,
        String encryptedPrivateKey
) {
}
