package ru.kubsu.borshchevyk.user.application.dto.command;

import lombok.Builder;

/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
public record SearchUsersCommand(
    String query,
    String requesterId
) {
    @Builder
    public SearchUsersCommand {}
}

