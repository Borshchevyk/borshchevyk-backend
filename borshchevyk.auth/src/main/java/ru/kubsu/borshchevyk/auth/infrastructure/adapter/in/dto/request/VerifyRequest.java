package ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Request to verify challenge signature and obtain tokens.
 * @param userId    UUID of the user being authenticated
 * @param signature Base64 encoded RSA signature of the challenge
 */
@Schema(description = "Request to verify challenge signature and obtain tokens")
public record VerifyRequest(
        @NotNull(message = "User ID is mandatory")
        @Schema(
                description = "UUID of the user being authenticated",
                example = "550e8400-e29b-41d4-a716-446655440000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        UUID userId,
        @NotBlank(message = "Signature is mandatory")
        @Schema(
                description = "Base64 encoded RSA signature of the challenge",
                example = "MIIBIjAN...",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String signature
) { }
