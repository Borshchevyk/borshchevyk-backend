package ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
import java.util.UUID;

/**
 * Request DTO for initiating a new call.
 */
@Schema(description = "Request object for initiating a new call")
public record InitiateCallRequest(
        @Schema(description = "Set of user IDs to invite to the call", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"123e4567-e89b-12d3-a456-426614174000\", \"123e4567-e89b-12d3-a456-426614174001\"]")
        @NotNull(message = "Participant IDs cannot be null")
        @NotEmpty(message = "Participant IDs cannot be empty")
        Set<UUID> participantIds
) {}
