package ru.kubsu.borshchevyk.user.application.port.in;

import ru.kubsu.borshchevyk.user.application.dto.command.RemoveContactCommand;

/**
 * Port for removing a contact from a user's contact list.
 *
 * @author Aleksey Timko
 */
public interface RemoveContactUseCase {

    /**
     * Removes a contact from the user's contact list.
     *
     * @param command the command containing information about the contact to remove
     */
    void removeContact(RemoveContactCommand command);
}

