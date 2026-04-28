package ru.kubsu.borshchevyk.message.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatRole;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "chat_members", indexes = {
        @Index(name = "idx_chat_member_user", columnList = "user_id")
})
@IdClass(ChatMemberId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMemberEntity {

    @Id
    @Column(name = "chat_id")
    private UUID chatId;

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private ChatRole role;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Column(name = "can_send_messages", nullable = false)
    @Builder.Default
    private boolean canSendMessages = true;

    @Column(name = "can_delete_messages", nullable = false)
    @Builder.Default
    private boolean canDeleteMessages = true;

    @Column(name = "can_invite_users", nullable = false)
    @Builder.Default
    private boolean canInviteUsers = true;

    @Column(name = "can_change_info", nullable = false)
    @Builder.Default
    private boolean canChangeInfo = true;

    @Column(name = "history_cleared_at")
    private LocalDateTime historyClearedAt;

    @Column(name = "last_read_message_id")
    private java.util.UUID lastReadMessageId;

    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt;

    @Column(name = "is_pinned", nullable = false)
    @Builder.Default
    private boolean isPinned = false;
}
