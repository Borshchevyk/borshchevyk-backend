package ru.kubsu.borshchevyk.auth.application.dto.command;

import lombok.Builder;

/**
 * Command for user login.
 *
 * @param email        user's email address
 * @param passwordHash client-side password hash (not raw password)
 */
@Builder
public record LoginCommand(
        String email,
        String passwordHash
) {
}
