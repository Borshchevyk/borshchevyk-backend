package ru.kubsu.borshchevyk.user.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.user.application.dto.command.LoadContactsCommand;
import ru.kubsu.borshchevyk.user.application.port.in.LoadContactsUseCase;
import ru.kubsu.borshchevyk.user.application.port.out.ContactPort;
import ru.kubsu.borshchevyk.user.domain.model.contact.Contact;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

import java.util.List;
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
public class LoadContactsService implements LoadContactsUseCase {
    private final ContactPort contactPort;

    @Override
    public List<Contact> loadContacts(LoadContactsCommand command) {
        UserId ownerId = new UserId(UUID.fromString(command.ownerId()));
        return contactPort.loadByOwnerId(ownerId);
    }
}

