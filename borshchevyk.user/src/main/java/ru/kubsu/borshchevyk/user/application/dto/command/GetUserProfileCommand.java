package ru.kubsu.borshchevyk.user.application.dto.command;

import lombok.Builder;

/**
 * Command for retrieving a user's profile.
 *
 * @param targetUserIdOrTag the unique identifier or tag of the user whose profile is being requested
 * @param requesterId       the unique identifier of the user making the request
 * @author Aleksey Timko
 */
@Builder
public record GetUserProfileCommand(
    String targetUserIdOrTag,
    String requesterId
) {
}
