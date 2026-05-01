package ru.kubsu.borshchevyk.message.domain.model.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Domain aggregate root representing a Message.
 *
 * @author Aleksey Timko
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    private MessageId id;
    private ChatId chatId;
    private UserId authorId;
    private String text;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted;
    private MessageSource source;
    @Builder.Default
    private MessageStatus status = MessageStatus.RECEIVED_BY_SERVER;
    private LocalDateTime pinnedAt;
    private UserId pinnedBy;
    private ChatId forwardedFromChatId;
    private UserId forwardedFromUserId;
    private MessageId parentMessageId;
    @Builder.Default
    private int commentsCount = 0;
    @Builder.Default
    private List<MessageReaction> reactions = new ArrayList<>();
    @Builder.Default
    private List<MessageAttachment> attachments = new ArrayList<>();
}
