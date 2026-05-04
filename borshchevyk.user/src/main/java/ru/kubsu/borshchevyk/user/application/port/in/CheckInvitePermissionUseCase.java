package ru.kubsu.borshchevyk.user.application.port.in;

import ru.kubsu.borshchevyk.user.application.dto.command.CheckInvitePermissionCommand;

/**
 * Use case for checking if a user can invite another user to a chat.
 *
 * @author Aleksey Timko
 */
public interface CheckInvitePermissionUseCase {
    boolean checkInvitePermission(CheckInvitePermissionCommand command);
}
