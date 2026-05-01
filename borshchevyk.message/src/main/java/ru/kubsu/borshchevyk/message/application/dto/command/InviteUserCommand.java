package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;
import java.util.UUID;

/**
 * Command to invite a user to a chat.
 *
 * @author Aleksey Timko
 */
@Builder
public record InviteUserCommand(
    UUID chatId,
    UUID requesterId,
    UUID targetUserId
) {
}
