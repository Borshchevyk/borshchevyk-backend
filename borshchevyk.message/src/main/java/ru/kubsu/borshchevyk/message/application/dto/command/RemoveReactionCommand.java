package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;
import java.util.UUID;

/**
 * Command to remove a reaction from a message.
 *
 * @author Aleksey Timko
 */
@Builder
public record RemoveReactionCommand(
    UUID chatId,
    UUID messageId,
    UUID requesterId,
    String reaction
) {
}
