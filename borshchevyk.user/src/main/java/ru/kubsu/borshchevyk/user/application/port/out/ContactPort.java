package ru.kubsu.borshchevyk.user.application.port.out;

import ru.kubsu.borshchevyk.user.domain.model.contact.Contact;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

import java.util.List;

/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
public interface ContactPort {
    Contact save(Contact contact);
    void remove(Contact contact);
    List<Contact> loadByOwnerId(UserId ownerId);
    boolean isContact(UserId ownerId, UserId contactUserId);
}

