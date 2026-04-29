package ru.kubsu.borshchevyk.user.application.dto.command;

import lombok.Builder;

/**
 * Command object carrying data for updating an existing user's profile.
 *
 * @param userId the unique identifier of the user to edit
 * @param email  the new email address (optional)
 * @param tag    the new user tag (optional)
 * @author Aleksey Timko
 */
@Builder
public record EditUserCommand(
    String userId,
    String email,
    String tag
) {
}
