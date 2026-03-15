package ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request to register a new user.
 *
 * @param email               User's email address
 * @param tag                 Unique user tag
 * @param passwordHash        Client-side AuthHash (not the raw password)
 * @param publicKey           Base64 encoded RSA Public Key
 * @param encryptedPrivateKey Base64 encoded encrypted Private Key
 * @author Aleksey Timko
 * @since 2026-03-14
 */
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
