package ru.kubsu.borshchevyk.user.application.port.in;

import ru.kubsu.borshchevyk.user.application.dto.command.AddContactCommand;
import ru.kubsu.borshchevyk.user.domain.model.contact.Contact;

/**
 * Port for adding a contact to a user's contact list.
 *
 * @author Aleksey Timko
 */
public interface AddContactUseCase {

    /**
     * Adds a contact to the user's contact list.
     *
     * @param command the command containing information about the contact to add
     * @return the added contact details
     */
    Contact addContact(AddContactCommand command);
}

