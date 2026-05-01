package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;
import java.util.UUID;

/**
 * Command to update permissions of a user in a chat.
 *
 * @author Aleksey Timko
 */
@Builder
public record UpdatePermissionsCommand(
    UUID chatId,
    UUID targetUserId,
    UUID requesterId,
    Boolean canSendMessages,
    Boolean canDeleteMessages,
    Boolean canInviteUsers,
    Boolean canChangeInfo
) {
}
