package ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response DTO for joining a call.
 */
@Schema(description = "Response object containing access token to join a call")
public record JoinCallResponse(
        @Schema(description = "LiveKit JWT Access Token to join the WebRTC room", example = "eyJhbGciOiJIUzI1NiIsInR5cCI...", requiredMode = Schema.RequiredMode.REQUIRED)
        String token
) {}
