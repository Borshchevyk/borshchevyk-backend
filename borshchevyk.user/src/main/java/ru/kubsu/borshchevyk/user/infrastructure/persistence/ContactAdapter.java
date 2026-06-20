package ru.kubsu.borshchevyk.user.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.user.application.port.out.ContactPort;
import ru.kubsu.borshchevyk.user.domain.model.contact.Contact;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;
import ru.kubsu.borshchevyk.user.infrastructure.persistence.entity.ContactEntity;
import ru.kubsu.borshchevyk.user.infrastructure.persistence.repository.ContactRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Persistence adapter for managing user contacts in the database.
 *
 * @author Aleksey Timko
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ContactAdapter implements ContactPort {

    private final ContactRepository repository;

    @Override
    public Contact save(Contact contact) {
        log.debug("Saving contact for owner ID: {} and contact user ID: {}", contact.getOwnerId().getValue(), contact.getContactUserId().getValue());
        ContactEntity entity = ContactEntity.builder()
                .id(contact.getId())
                .ownerId(contact.getOwnerId().getValue())
                .contactUserId(contact.getContactUserId().getValue())
                .contactFirstName(contact.getContactFirstName())
                .contactLastName(contact.getContactLastName())
                .addedAt(contact.getAddedAt())
                .build();

        ContactEntity saved = repository.save(entity);
        log.info("Successfully saved contact for owner ID: {}", contact.getOwnerId().getValue());
        return mapToDomain(saved);
    }

    @Override
    public void remove(Contact contact) {
        log.debug("Removing contact for owner ID: {}", contact.getOwnerId().getValue());
        if (contact.getId() != null) {
            repository.deleteById(contact.getId());
        } else {
            repository.deleteByOwnerIdAndContactUserId(contact.getOwnerId().getValue(), contact.getContactUserId().getValue());
        }
        log.info("Successfully removed contact for owner ID: {}", contact.getOwnerId().getValue());
    }

    @Override
    public List<Contact> loadByOwnerId(UserId ownerId) {
        log.debug("Loading contacts for owner ID: {}", ownerId.getValue());
        return repository.findByOwnerId(ownerId.getValue()).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isContact(UserId ownerId, UserId contactUserId) {
        log.debug("Checking if user {} is contact of owner {}", contactUserId.getValue(), ownerId.getValue());
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
