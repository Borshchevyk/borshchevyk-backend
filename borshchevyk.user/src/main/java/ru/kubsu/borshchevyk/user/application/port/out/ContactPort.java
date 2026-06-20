package ru.kubsu.borshchevyk.user.application.port.out;

import ru.kubsu.borshchevyk.user.domain.model.contact.Contact;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

import java.util.List;

/**
 * Outbound port for managing user contacts in the persistent store.
 *
 * @author Aleksey Timko
 */
public interface ContactPort {
    
    /**
     * Saves a contact.
     *
     * @param contact the contact to save
     * @return the saved contact
     */
    Contact save(Contact contact);
    
    /**
     * Removes an existing contact.
     *
     * @param contact the contact to remove
     */
    void remove(Contact contact);
    
    /**
     * Loads all contacts belonging to a specific owner.
     *
     * @param ownerId the user ID of the contact owner
     * @return a list of contacts owned by the user
     */
    List<Contact> loadByOwnerId(UserId ownerId);
    
    /**
     * Checks if a user is a contact of the owner.
     *
     * @param ownerId the user ID of the owner
     * @param contactUserId the user ID of the contact to check
     * @return true if the contact exists, false otherwise
     */
    boolean isContact(UserId ownerId, UserId contactUserId);
}

