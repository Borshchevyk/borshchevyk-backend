package ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Response containing JWT tokens after successful signature verification")
public record VerifyResponse(
        @Schema(description = "JWT Access Token", example = "eyJhbGciOiJIUzI1NiJ9...")
        String accessToken,
        @Schema(description = "JWT Refresh Token", example = "eyJhbGciOiJIUzI1NiJ9...")
        String refreshToken
) { }
