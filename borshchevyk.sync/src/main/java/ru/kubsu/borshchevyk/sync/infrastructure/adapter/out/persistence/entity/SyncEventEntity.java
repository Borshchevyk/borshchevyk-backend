package ru.kubsu.borshchevyk.sync.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity representing a synchronization event.
 *
 * @author Aleksey Timko
 */
@Entity
@Table(name = "sync_events", indexes = {
    @Index(name = "idx_target_user_sequence", columnList = "target_user_id, sequence_number")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "target_user_id", nullable = false)
    private UUID targetUserId;

    @Column(name = "sequence_number", nullable = false)
    private Long sequenceNumber;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}