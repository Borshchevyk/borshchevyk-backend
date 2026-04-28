package ru.kubsu.borshchevyk.user.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.user.application.port.out.ContactPort;
import ru.kubsu.borshchevyk.user.domain.model.contact.Contact;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;
import ru.kubsu.borshchevyk.user.infrastructure.persistence.entity.ContactEntity;
import ru.kubsu.borshchevyk.user.infrastructure.persistence.repository.ContactRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
@Component
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
public class ContactAdapter implements ContactPort {

    private final ContactRepository repository;

    @Override
    public Contact save(Contact contact) {
        ContactEntity entity = ContactEntity.builder()
                .id(contact.getId())
                .ownerId(contact.getOwnerId().getValue())
                .contactUserId(contact.getContactUserId().getValue())
                .contactFirstName(contact.getContactFirstName())
                .contactLastName(contact.getContactLastName())
                .addedAt(contact.getAddedAt())
                .build();

        ContactEntity saved = repository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public void remove(Contact contact) {
        if (contact.getId() != null) {
            repository.deleteById(contact.getId());
        } else {
            repository.deleteByOwnerIdAndContactUserId(contact.getOwnerId().getValue(), contact.getContactUserId().getValue());
        }
    }

    @Override
    public List<Contact> loadByOwnerId(UserId ownerId) {
        return repository.findByOwnerId(ownerId.getValue()).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isContact(UserId ownerId, UserId contactUserId) {
        return repository.existsByOwnerIdAndContactUserId(ownerId.getValue(), contactUserId.getValue());
    }

    private Contact mapToDomain(ContactEntity entity) {
        return Contact.builder()
                .id(entity.getId())
                .ownerId(new UserId(entity.getOwnerId()))
                .contactUserId(new UserId(entity.getContactUserId()))
                .contactFirstName(entity.getContactFirstName())
                .contactLastName(entity.getContactLastName())
                .addedAt(entity.getAddedAt())
                .build();
    }
}

