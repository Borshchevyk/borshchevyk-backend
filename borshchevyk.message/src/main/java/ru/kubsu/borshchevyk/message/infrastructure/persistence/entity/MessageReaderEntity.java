package ru.kubsu.borshchevyk.message.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Aleksey Timko
 * @since 2026-05-01
 */
@Entity
@Table(name = "message_readers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(MessageReaderId.class)
public class MessageReaderEntity {

    @Id
    @Column(name = "message_id", nullable = false)
    private UUID messageId;

    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "read_at", nullable = false)
    private LocalDateTime readAt;
}
