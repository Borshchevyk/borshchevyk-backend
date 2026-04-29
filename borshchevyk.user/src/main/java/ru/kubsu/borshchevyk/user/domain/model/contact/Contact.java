package ru.kubsu.borshchevyk.user.domain.model.contact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain entity representing a user's contact list entry.
 *
 * @author Aleksey Timko
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Contact {

    /**
     * Unique identifier for the contact entry.
     */
    @EqualsAndHashCode.Include
    private UUID id;

    /**
     * The ID of the user who owns this contact.
     */
    private UserId ownerId;

    /**
     * The ID of the user who is added as a contact.
     */
    private UserId contactUserId;

    /**
     * The first name of the contact.
     */
    private String contactFirstName;

    /**
     * The last name of the contact.
     */
    private String contactLastName;

    /**
     * The timestamp when the contact was added.
     */
    private LocalDateTime addedAt;
}
