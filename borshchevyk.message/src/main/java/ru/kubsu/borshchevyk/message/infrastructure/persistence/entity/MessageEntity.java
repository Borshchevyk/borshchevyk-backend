package ru.kubsu.borshchevyk.message.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import ru.kubsu.borshchevyk.message.domain.model.message.MessageSource;
import ru.kubsu.borshchevyk.message.domain.model.message.MessageStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "messages", indexes = {
        @Index(name = "idx_message_chat_created", columnList = "chat_id, created_at DESC"),
        @Index(name = "idx_message_chat_parent", columnList = "chat_id, parent_message_id, created_at ASC"),
        @Index(name = "idx_message_chat_pinned", columnList = "chat_id, pinned_at DESC")
})
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

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private MessageSource source;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MessageStatus status = MessageStatus.RECEIVED_BY_SERVER;

    @Column(name = "pinned_at")
    private LocalDateTime pinnedAt;

    @Column(name = "pinned_by")
    private UUID pinnedBy;

    @Column(name = "forwarded_from_chat_id")
    private UUID forwardedFromChatId;

    @Column(name = "forwarded_from_user_id")
    private UUID forwardedFromUserId;

    @Column(name = "parent_message_id")
    private UUID parentMessageId;

    @Column(name = "comments_count", nullable = false)
    @Builder.Default
    private int commentsCount = 0;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "message_reactions", joinColumns = @JoinColumn(name = "message_id"))
    @AttributeOverrides({
            @AttributeOverride(name = "userId", column = @Column(name = "user_id", nullable = false)),
            @AttributeOverride(name = "reaction", column = @Column(name = "reaction", nullable = false))
    })
    @Builder.Default
    private List<MessageReactionEmbeddable> reactions = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "message_attachments", joinColumns = @JoinColumn(name = "message_id"))
    @Builder.Default
    private List<MessageAttachmentEmbeddable> attachments = new ArrayList<>();
}
