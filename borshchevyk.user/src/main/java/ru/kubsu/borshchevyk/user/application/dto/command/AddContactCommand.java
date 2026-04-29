package ru.kubsu.borshchevyk.user.application.dto.command;

import lombok.Builder;

/**
 * Command for adding a user to the contact list.
 *
 * @param ownerId      the unique identifier of the user adding the contact
 * @param targetUserId the unique identifier of the user being added
 * @param firstName    the first name of the contact
 * @param lastName     the last name of the contact
 * @author Aleksey Timko
 */
@Builder
public record AddContactCommand(
    String ownerId,
    String targetUserId,
    String firstName,
    String lastName
) {
}
