package ru.kubsu.borshchevyk.user.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kubsu.borshchevyk.user.domain.model.privacy.Visibility;

import java.util.UUID;

/**
 * JPA entity representing a user's privacy settings in the database.
 *
 * @author Aleksey Timko
 */
@Entity
@Table(name = "privacy_settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrivacySettingsEntity {

    @Id
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "email_visibility", nullable = false)
    private Visibility emailVisibility;

    @Enumerated(EnumType.STRING)
    @Column(name = "search_by_email_visibility", nullable = false)
    private Visibility searchByEmailVisibility;

    @Enumerated(EnumType.STRING)
    @Column(name = "profile_photo_visibility", nullable = false)
    private Visibility profilePhotoVisibility;

    @Enumerated(EnumType.STRING)
    @Column(name = "invite_to_chat_visibility", nullable = false)
    private Visibility inviteToChatVisibility;
}
