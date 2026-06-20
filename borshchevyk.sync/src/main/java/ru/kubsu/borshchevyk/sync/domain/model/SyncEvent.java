package ru.kubsu.borshchevyk.sync.domain.model;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain model representing a synchronization event in the CRDT/Vector Clock architecture.
 *
 * @author Aleksey Timko
 * @since 2026-06-18
 */
@Builder
public record SyncEvent(
        UUID id,
        UUID entityId,
        UUID userId,
        EventType eventType,
        String payload,
        VectorClock vectorClock,
        Instant timestamp
) {
}
