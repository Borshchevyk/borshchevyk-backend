package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;
import java.util.UUID;

/**
 * Command to update a message.
 *
 * @author Aleksey Timko
 */
@Builder
public record UpdateMessageCommand(
    UUID messageId,
    UUID chatId,
    UUID requesterId,
    String text
) {
}
