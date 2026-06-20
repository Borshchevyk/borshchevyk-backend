package ru.kubsu.borshchevyk.user.domain.model.privacy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

/**
 * Domain entity representing a user's privacy settings.
 *
 * @author Aleksey Timko
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PrivacySettings {

    /**
     * The ID of the user these settings belong to.
     */
    @EqualsAndHashCode.Include
    private UserId userId;
    
    /**
     * Visibility setting for the user's email.
     */
    @Builder.Default
    private Visibility emailVisibility = Visibility.NOBODY;
    
    /**
     * Visibility setting for searching the user by email.
     */
    @Builder.Default
    private Visibility searchByEmailVisibility = Visibility.EVERYONE;
    
    /**
     * Visibility setting for the user's profile photo.
     */
    @Builder.Default
    private Visibility profilePhotoVisibility = Visibility.EVERYONE;
    
    /**
     * Visibility setting for inviting the user to a chat.
     */
    @Builder.Default
    private Visibility inviteToChatVisibility = Visibility.EVERYONE;
}
