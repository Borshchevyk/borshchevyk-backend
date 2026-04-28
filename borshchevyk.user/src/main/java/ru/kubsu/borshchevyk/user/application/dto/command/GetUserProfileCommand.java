package ru.kubsu.borshchevyk.user.application.dto.command;

import lombok.Builder;

/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
public record GetUserProfileCommand(
    String targetUserIdOrTag,
    String requesterId
) {
    @Builder
    public GetUserProfileCommand {}
}

