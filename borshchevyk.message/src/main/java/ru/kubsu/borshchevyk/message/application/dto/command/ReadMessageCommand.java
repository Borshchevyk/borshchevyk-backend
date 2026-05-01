package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;
import java.util.UUID;

/**
 * Command to mark a message as read.
 *
 * @author Aleksey Timko
 */
@Builder
public record ReadMessageCommand(
    UUID chatId,
    UUID messageId,
    UUID requesterId
) {
}
