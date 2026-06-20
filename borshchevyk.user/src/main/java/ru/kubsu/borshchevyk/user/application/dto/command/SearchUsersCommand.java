package ru.kubsu.borshchevyk.user.application.dto.command;

import lombok.Builder;

/**
 * Command for searching users based on a query string.
 *
 * @param query       the search query
 * @param requesterId the unique identifier of the user making the search request
 * @author Aleksey Timko
 */
@Builder
public record SearchUsersCommand(
    String query,
    String requesterId
) {
}
