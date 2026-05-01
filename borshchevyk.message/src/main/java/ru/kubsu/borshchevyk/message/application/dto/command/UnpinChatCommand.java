package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;
import java.util.UUID;

/**
 * Command to unpin a chat.
 *
 * @author Aleksey Timko
 */
@Builder
public record UnpinChatCommand(
    UUID requesterId,
    UUID chatId
) {
}
