package ru.kubsu.borshchevyk.sync.domain.model;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing a synchronization event.
 *
 * @author Aleksey Timko
 * @since 2026-03-01
 */
@Builder
public record SyncEvent(
        UUID eventId,
        UUID targetUserId,
        Long sequenceNumber,
        EventType eventType,
        String payload,
        LocalDateTime createdAt
) {
}
