package ru.kubsu.borshchevyk.auth.application.port.in;

import ru.kubsu.borshchevyk.auth.application.dto.command.ChangePasswordCommand;

/**
 * Use case for changing a user's password.
 */
public interface ChangePasswordUseCase {
    /**
     * Changes the password for an account.
     *
     * @param command the change password command
     */
    void changePassword(ChangePasswordCommand command);
}
