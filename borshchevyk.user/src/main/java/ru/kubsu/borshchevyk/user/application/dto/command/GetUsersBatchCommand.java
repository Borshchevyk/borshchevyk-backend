package ru.kubsu.borshchevyk.user.application.dto.command;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

/**
 * Command for retrieving a batch of users by their IDs.
 *
 * @param userIds     the list of unique identifiers of the users to retrieve
 * @param requesterId the unique identifier of the user making the request
 * @author Aleksey Timko
 */
@Builder
public record GetUsersBatchCommand(
    List<UUID> userIds,
    String requesterId
) {
}
