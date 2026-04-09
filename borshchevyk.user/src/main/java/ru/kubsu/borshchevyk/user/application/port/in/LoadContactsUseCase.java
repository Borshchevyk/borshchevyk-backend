package ru.kubsu.borshchevyk.user.application.port.in;

import ru.kubsu.borshchevyk.user.application.dto.command.LoadContactsCommand;
import ru.kubsu.borshchevyk.user.domain.model.contact.Contact;
import java.util.List;

public interface LoadContactsUseCase {
    List<Contact> loadContacts(LoadContactsCommand command);
}
