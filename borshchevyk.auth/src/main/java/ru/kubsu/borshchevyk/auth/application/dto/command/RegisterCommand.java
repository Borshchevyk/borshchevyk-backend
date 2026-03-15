package ru.kubsu.borshchevyk.auth.application.dto.command;

import lombok.Builder;

/**
 * Command for user registration.
 *
 * @param email               user's email address
 * @param tag                 user's unique tag
 * @param passwordHash        client-side password hash
 * @param publicKey           user's public key
 * @param encryptedPrivateKey user's encrypted private key
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Builder
public record RegisterCommand(
        String email,
        String tag,
        String passwordHash,
        String publicKey,
        String encryptedPrivateKey
) { }
