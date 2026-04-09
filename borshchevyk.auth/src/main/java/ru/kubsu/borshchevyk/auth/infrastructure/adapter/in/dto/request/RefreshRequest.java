package ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO for refreshing an access token.
 *
 * @param refreshToken the refresh token
 */
@Schema(description = "Request to refresh an access token")
public record RefreshRequest(
        @Schema(description = "Refresh token string", requiredMode = Schema.RequiredMode.REQUIRED)
        String refreshToken
) {}
