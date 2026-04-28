package ru.kubsu.borshchevyk.user.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.user.application.dto.command.RemoveContactCommand;
import ru.kubsu.borshchevyk.user.application.port.in.RemoveContactUseCase;
import ru.kubsu.borshchevyk.user.application.port.out.ContactPort;
import ru.kubsu.borshchevyk.user.domain.model.contact.Contact;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

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
public class RemoveContactService implements RemoveContactUseCase {
    private final ContactPort contactPort;

    @Override
    public void removeContact(RemoveContactCommand command) {
        UserId ownerId = new UserId(UUID.fromString(command.ownerId()));
        UserId targetId = new UserId(UUID.fromString(command.targetUserId()));
        
        Contact contact = Contact.builder()
            .ownerId(ownerId)
            .contactUserId(targetId)
            .build();
            
        contactPort.remove(contact);
    }
}

