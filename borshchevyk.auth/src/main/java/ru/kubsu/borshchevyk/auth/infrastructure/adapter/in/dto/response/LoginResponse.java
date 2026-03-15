package ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.UUID;

@Builder
@Schema(description = "Successful login response (Phase 1)")
public record LoginResponse(
        @Schema(description = "Internal user UUID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID userId,
        @Schema(description = "Base64 encoded RSA Public Key", example = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...")
        String publicKey,
        @Schema(description = "Base64 encoded encrypted Private Key", example = "v2...")
        String encryptedPrivateKey
) { }
