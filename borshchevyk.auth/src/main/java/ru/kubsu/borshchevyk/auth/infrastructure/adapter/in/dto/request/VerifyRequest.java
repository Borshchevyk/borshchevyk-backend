package ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

/**
 * Request to verify challenge signature and obtain tokens.
 *
 * @param userId    UUID of the user being authenticated
 * @param signature Base64 encoded RSA signature of the challenge
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Schema(description = "Request to verify challenge signature and obtain tokens")
public record VerifyRequest(
        @Schema(description = "UUID of the user being authenticated", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID userId,
        @Schema(description = "Base64 encoded RSA signature of the challenge", example = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...")
        String signature
) { }
