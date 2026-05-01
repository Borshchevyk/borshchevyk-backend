package ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO representing a synchronization event for the presentation layer.
 *
 * @author Aleksey Timko
 */
@Builder
@Schema(description = "DTO representing a synchronization event")
public record SyncEventDto(
        @Schema(description = "Unique identifier of the event", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID eventId,

        @Schema(description = "ID of the user to whom this event is targeted", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID targetUserId,

        @Schema(description = "Sequential number of the event for the target user", example = "42")
        Long sequenceNumber,

        @Schema(description = "Type of the event", example = "MESSAGE_CREATED")
        String eventType,

        @Schema(description = "JSON payload of the event", example = "{\"messageId\": \"...\"}")
        String payload,

        @Schema(description = "Timestamp when the event was created", example = "2026-03-01T12:00:00")
        LocalDateTime createdAt
) {
}
