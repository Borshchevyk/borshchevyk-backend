package ru.kubsu.borshchevyk.calls.infrastructure.adapter.out.messaging.dto;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for Kafka events indicating a change in call status.
 *
 * @author Aleksey Timko
 * @since 2026-04-25
 */
@Data
@Builder
public class CallEventMessage {
    private UUID callId;
    private String eventType; // INITIATED, ENDED
    private UUID initiatorId;
    private Instant timestamp;
    private Set<UUID> participants;
}
