package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;
import java.util.UUID;

/**
 * Command to add a reaction to a message.
 *
 * @author Aleksey Timko
 */
@Builder
public record AddReactionCommand(
    UUID chatId,
    UUID messageId,
    UUID requesterId,
    String reaction
) {
}
