package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;
import java.util.UUID;

/**
 * Command to unpin a message.
 *
 * @author Aleksey Timko
 */
@Builder
public record UnpinMessageCommand(
    UUID chatId,
    UUID messageId,
    UUID requesterId
) {
}
