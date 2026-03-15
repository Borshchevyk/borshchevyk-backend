package ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to register a new user")
public record RegisterRequest(
        @Schema(description = "User's email address", example = "user@example.com")
        String email,
        @Schema(description = "Unique user tag", example = "john_doe")
        String tag,
        @Schema(description = "Client-side AuthHash (not the raw password)", example = "e3b0c442...")
        String passwordHash,
        @Schema(description = "Base64 encoded RSA Public Key", example = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...")
        String publicKey,
        @Schema(description = "Base64 encoded encrypted Private Key", example = "v2...")
        String encryptedPrivateKey
) { }