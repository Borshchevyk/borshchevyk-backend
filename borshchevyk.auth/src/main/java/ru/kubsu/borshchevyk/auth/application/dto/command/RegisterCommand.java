package ru.kubsu.borshchevyk.auth.application.dto.command;

import lombok.Builder;

@Builder
public record RegisterCommand(
        String email,
        String tag,
        String passwordHash,
        String publicKey,
        String encryptedPrivateKey
) { }
