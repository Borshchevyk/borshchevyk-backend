package ru.kubsu.borshchevyk.message.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.kubsu.borshchevyk.message.domain.model.notification.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity for persistent notifications.
 *
 * @author Aleksey Timko
 */
@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppNotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
