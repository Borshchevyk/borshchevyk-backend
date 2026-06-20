package ru.kubsu.borshchevyk.sync.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.kubsu.borshchevyk.sync.domain.model.EventType;
import ru.kubsu.borshchevyk.sync.domain.model.VectorClock;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity representing a synchronization event in the CRDT/Vector Clock architecture.
 *
 * @author Aleksey Timko
 */
@Entity
@Table(name = "sync_event_journal", indexes = {
    @Index(name = "idx_sync_entity_id", columnList = "entity_id"),
    @Index(name = "idx_sync_user_created", columnList = "user_id, created_at")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncEventEntity {

    @Id
    private UUID id;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Column(name = "user_id", nullable = true)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private String payload;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "vector_clock", columnDefinition = "jsonb", nullable = false)
    private VectorClock vectorClock;

    @Column(name = "created_at", nullable = false)
    private Instant timestamp;
}
