package ru.kubsu.borshchevyk.user.application.dto.command;

import lombok.Builder;

/**
 * Command for loading the contact list of a user.
 *
 * @param ownerId the unique identifier of the user whose contacts are being loaded
 * @author Aleksey Timko
 */
@Builder
public record LoadContactsCommand(
    String ownerId
) {
}
