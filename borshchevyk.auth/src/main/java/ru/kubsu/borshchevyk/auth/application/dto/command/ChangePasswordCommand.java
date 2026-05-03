package ru.kubsu.borshchevyk.auth.application.dto.command;

import lombok.Builder;

/**
 * Command for changing a user password.
 *
 * @param email       user's email address
 * @param oldPassword the user's old password
 * @param newPassword the user's new password
 */
@Builder
public record ChangePasswordCommand(
        String email,
        String oldPassword,
        String newPassword
) {
}