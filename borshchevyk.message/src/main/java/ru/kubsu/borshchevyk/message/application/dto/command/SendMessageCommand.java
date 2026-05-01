package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;
import ru.kubsu.borshchevyk.message.domain.model.message.MessageSource;

import java.util.List;
import java.util.UUID;

/**
 * Command to send a new message.
 *
 * @author Aleksey Timko
 */
@Builder
public record SendMessageCommand(
    UUID chatId,
    UUID authorId,
    String text,
    MessageSource source,
    UUID forwardedFromChatId,
    UUID forwardedFromUserId,
    UUID parentMessageId,
    List<UUID> attachmentIds
) {
}
