package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;
import java.util.UUID;

/**
 * Command to delete a chat.
 *
 * @author Aleksey Timko
 */
@Builder
public record DeleteChatCommand(
    UUID chatId,
    UUID requesterId
) {
}
