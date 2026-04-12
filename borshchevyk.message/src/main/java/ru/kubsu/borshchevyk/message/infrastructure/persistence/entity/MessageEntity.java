package ru.kubsu.borshchevyk.message.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import ru.kubsu.borshchevyk.message.domain.model.message.MessageSource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "chat_id", nullable = false)
    private UUID chatId;

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private MessageSource source;

    @Column(name = "pinned_at")
    private LocalDateTime pinnedAt;

    @Column(name = "pinned_by")
    private UUID pinnedBy;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "message_reactions", joinColumns = @JoinColumn(name = "message_id"))
    @AttributeOverrides({
            @AttributeOverride(name = "userId", column = @Column(name = "user_id", nullable = false)),
            @AttributeOverride(name = "reaction", column = @Column(name = "reaction", nullable = false))
    })
    @Builder.Default
    private List<MessageReactionEmbeddable> reactions = new ArrayList<>();
}
