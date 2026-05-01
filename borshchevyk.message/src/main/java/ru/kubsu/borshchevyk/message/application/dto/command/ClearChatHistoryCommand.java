package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;
import java.util.UUID;

/**
 * Command to clear the history of a chat.
 *
 * @author Aleksey Timko
 */
@Builder
public record ClearChatHistoryCommand(
    UUID chatId,
    UUID requesterId,
    boolean forAll
) {
}
