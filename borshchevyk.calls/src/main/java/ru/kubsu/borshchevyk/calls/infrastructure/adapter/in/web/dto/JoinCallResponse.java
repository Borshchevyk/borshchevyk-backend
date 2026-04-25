package ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response DTO for joining a call.
 *
 * @author Gemini
 * @since 2026-04-25
 */
@Schema(description = "Response object containing access token to join a call")
public record JoinCallResponse(
        @Schema(description = "LiveKit JWT Access Token to join the WebRTC room")
        String token
) {}
