package ru.kubsu.borshchevyk.user.application.dto.command;

import lombok.Builder;

/**
 * Command to check if a user can invite another user to a chat.
 *
 * @author Aleksey Timko
 */
@Builder
public record CheckInvitePermissionCommand(
        String targetUserId,
        String requesterId
) {
}
