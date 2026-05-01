package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;
import java.util.UUID;

/**
 * Command to leave a chat.
 *
 * @author Aleksey Timko
 */
@Builder
public record LeaveChatCommand(
    UUID chatId,
    UUID requesterId
) {
}
