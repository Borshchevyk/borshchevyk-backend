package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;
import java.util.UUID;

/**
 * Command to pin a chat.
 *
 * @author Aleksey Timko
 */
@Builder
public record PinChatCommand(
    UUID requesterId,
    UUID chatId
) {
}
