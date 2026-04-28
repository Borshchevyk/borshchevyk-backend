package ru.kubsu.borshchevyk.calls.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.*;
import ru.kubsu.borshchevyk.calls.domain.model.CallStatus;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * JPA Entity representing a Call.
 *
 * @author Aleksey Timko
 * @since 2026-04-25
 */
@Entity
@Table(name = "calls")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CallEntity {
    @Id
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private String roomId;
    
    @Column(nullable = false)
    private UUID initiatorId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CallStatus status;
    
    @Column(nullable = false)
    private Instant createdAt;
    
    private Instant endedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "call_participants", joinColumns = @JoinColumn(name = "call_id"))
    @Column(name = "user_id")
    @Builder.Default
    private Set<UUID> participants = new HashSet<>();
}
