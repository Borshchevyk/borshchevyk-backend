package ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * Response containing JWT tokens after successful signature verification.
 *
 * @param accessToken  JWT Access Token
 * @param refreshToken JWT Refresh Token
 */
@Builder
@Schema(description = "Response containing JWT tokens after successful signature verification")
public record VerifyResponse(
        @Schema(description = "JWT Access Token", example = "eyJhbGciOiJIUzI1NiJ9...")
        String accessToken,
        @Schema(description = "JWT Refresh Token", example = "eyJhbGciOiJIUzI1NiJ9...")
        String refreshToken
) { }
