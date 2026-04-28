package ru.kubsu.borshchevyk.user.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.user.application.dto.command.AddContactCommand;
import ru.kubsu.borshchevyk.user.application.port.in.AddContactUseCase;
import ru.kubsu.borshchevyk.user.application.port.out.ContactPort;
import ru.kubsu.borshchevyk.user.domain.model.contact.Contact;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
@Slf4j
/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
@Service
/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
@RequiredArgsConstructor
/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
public class AddContactService implements AddContactUseCase {
    private final ContactPort contactPort;

    @Override
    public Contact addContact(AddContactCommand command) {
        UserId ownerId = new UserId(UUID.fromString(command.ownerId()));
        UserId targetId = new UserId(UUID.fromString(command.targetUserId()));
        
        if (contactPort.isContact(ownerId, targetId)) {
            log.warn("Contact already exists for owner {} and target {}", ownerId, targetId);
            throw new IllegalArgumentException("Contact already exists");
        }
        
        Contact contact = Contact.builder()
            .id(UUID.randomUUID())
            .ownerId(ownerId)
            .contactUserId(targetId)
            .contactFirstName(command.firstName())
            .contactLastName(command.lastName())
            .addedAt(LocalDateTime.now())
            .build();
            
        return contactPort.save(contact);
    }
}

