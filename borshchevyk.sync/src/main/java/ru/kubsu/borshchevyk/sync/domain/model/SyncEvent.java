package ru.kubsu.borshchevyk.sync.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing a synchronization event.
 *
 * @author Aleksey Timko
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Domain model representing a synchronization event")
public class SyncEvent {

    @Schema(description = "Unique identifier of the event", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID eventId;

    @Schema(description = "User ID to whom this event is directed", example = "123e4567-e89b-12d3-a456-426614174001")
    private UUID targetUserId;

    @Schema(description = "Sequential number of the event for the target user", example = "42")
    private Long sequenceNumber;

    @Schema(description = "Type of the event", example = "MESSAGE_CREATED")
    private EventType eventType;

    @Schema(description = "JSON payload containing event data", example = "{\"messageId\": \"...\"}")
    private String payload;

    @Schema(description = "Timestamp when the event was created", example = "2023-10-27T10:00:00")
    private LocalDateTime createdAt;
}
