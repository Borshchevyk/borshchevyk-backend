package ru.kubsu.borshchevyk.user.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
@Entity
/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
@Table(name = "contacts")
/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
@Data
/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
@Builder
/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
@NoArgsConstructor
/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
@AllArgsConstructor
/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
public class ContactEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "contact_user_id", nullable = false)
    private UUID contactUserId;

    @Column(name = "contact_first_name")
    private String contactFirstName;

    @Column(name = "contact_last_name")
    private String contactLastName;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;
}

