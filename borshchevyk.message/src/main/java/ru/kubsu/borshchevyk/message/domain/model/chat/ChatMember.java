package ru.kubsu.borshchevyk.message.domain.model.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.time.LocalDateTime;

/**
 * Domain model representing a member within a chat.
 * Contains the member's role and specific permissions.
 *
 * @author Aleksey Timko
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMember {
    private ChatId chatId;
    private UserId userId;
    private ChatRole role;
    private LocalDateTime joinedAt;
    @Builder.Default private boolean canSendMessages = true;
    @Builder.Default private boolean canDeleteMessages = true;
    @Builder.Default private boolean canInviteUsers = true;
    @Builder.Default private boolean canChangeInfo = true;
    private LocalDateTime historyClearedAt;
    private MessageId lastReadMessageId;
    private LocalDateTime lastReadAt;
    @Builder.Default private boolean isPinned = false;
}
