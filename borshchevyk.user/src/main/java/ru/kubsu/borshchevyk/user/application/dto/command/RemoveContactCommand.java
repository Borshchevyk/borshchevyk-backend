package ru.kubsu.borshchevyk.user.application.dto.command;

import lombok.Builder;

/**
 * Command for removing a user from the contact list.
 *
 * @param ownerId      the unique identifier of the user removing the contact
 * @param targetUserId the unique identifier of the contact being removed
 * @author Aleksey Timko
 */
@Builder
public record RemoveContactCommand(
    String ownerId,
    String targetUserId
) {
}
