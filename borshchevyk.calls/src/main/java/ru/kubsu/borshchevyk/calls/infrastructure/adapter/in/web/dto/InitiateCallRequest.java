package ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

/**
 * Request DTO for initiating a new call.
 *
 * @author Gemini
 * @since 2026-04-25
 */
@Schema(description = "Request object for initiating a new call")
public record InitiateCallRequest(
        @Schema(description = "Set of user IDs to invite to the call", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Participant IDs cannot be null")
        Set<UUID> participantIds
) {}
