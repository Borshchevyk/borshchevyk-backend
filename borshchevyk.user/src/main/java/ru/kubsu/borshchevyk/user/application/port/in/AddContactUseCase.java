package ru.kubsu.borshchevyk.user.application.port.in;

import ru.kubsu.borshchevyk.user.application.dto.command.AddContactCommand;
import ru.kubsu.borshchevyk.user.domain.model.contact.Contact;

/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
public interface AddContactUseCase {
    Contact addContact(AddContactCommand command);
}

