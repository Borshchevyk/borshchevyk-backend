package ru.kubsu.borshchevyk.user.application.dto.command;

import lombok.Builder;

/**
 * Command for updating a user's basic profile information.
 *
 * @param userId    the unique identifier of the user
 * @param firstName the new first name
 * @param lastName  the new last name
 * @param bio       the new biography or status
 * @param avatarUrl the URL of the new avatar image
 * @author Aleksey Timko
 */
@Builder
public record UpdateProfileCommand(
    String userId,
    String firstName,
    String lastName,
    String bio,
    String avatarUrl
) {
}
