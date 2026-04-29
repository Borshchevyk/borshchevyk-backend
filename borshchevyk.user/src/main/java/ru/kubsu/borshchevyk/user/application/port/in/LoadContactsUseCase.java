package ru.kubsu.borshchevyk.user.application.port.in;

import ru.kubsu.borshchevyk.user.application.dto.command.LoadContactsCommand;
import ru.kubsu.borshchevyk.user.domain.model.contact.Contact;
import java.util.List;

/**
 * Port for loading a user's contacts.
 *
 * @author Aleksey Timko
 */
public interface LoadContactsUseCase {

    /**
     * Loads the contacts for a user.
     *
     * @param command the command containing information for loading contacts
     * @return a list of the user's contacts
     */
    List<Contact> loadContacts(LoadContactsCommand command);
}

