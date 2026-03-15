package ru.kubsu.borshchevyk.auth.domain.model.result;

import lombok.Builder;

import java.util.UUID;

@Builder
public record LoginResult(
        UUID userId,
        String publicKey,
        String encryptedPrivateKey
) { }
