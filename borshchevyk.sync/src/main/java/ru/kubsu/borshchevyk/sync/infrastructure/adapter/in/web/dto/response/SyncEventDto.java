package ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ru.kubsu.borshchevyk.sync.domain.model.EventType;
import ru.kubsu.borshchevyk.sync.domain.model.VectorClock;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO representing a synchronization event for the presentation layer.
 *
 * @author Aleksey Timko
 */
@Builder
@Schema(description = "DTO representing a synchronization event with Vector Clock")
public record SyncEventDto(
        @Schema(description = "Unique identifier of the event", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "ID of the entity being mutated (e.g., Message ID, Chat ID)", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID entityId,

        @Schema(description = "Type of the event", example = "MESSAGE_CREATED")
        EventType eventType,

        @Schema(description = "JSON payload of the event (CRDT operations)", example = "{\"text\": \"hello\"}")
        String payload,

        @Schema(description = "Vector clock representing the causal history of this event")
        VectorClock vectorClock,

        @Schema(description = "Timestamp when the event was created", example = "2026-03-01T12:00:00Z")
        Instant timestamp
) {
}
