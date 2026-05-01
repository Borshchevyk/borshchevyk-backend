package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;
import java.util.UUID;

/**
 * Command to kick a user from a chat.
 *
 * @author Aleksey Timko
 */
@Builder
public record KickUserCommand(
    UUID chatId,
    UUID requesterId,
    UUID targetUserId
) {
}
