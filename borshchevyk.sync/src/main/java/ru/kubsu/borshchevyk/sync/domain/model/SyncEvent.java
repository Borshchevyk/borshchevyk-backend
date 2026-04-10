package ru.kubsu.borshchevyk.sync.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncEvent {
    private UUID eventId;
    private UUID targetUserId;
    private Long sequenceNumber;
    private EventType eventType;
    private String payload;
    private LocalDateTime createdAt;
}
