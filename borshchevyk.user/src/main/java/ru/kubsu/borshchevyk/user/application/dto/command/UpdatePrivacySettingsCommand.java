package ru.kubsu.borshchevyk.user.application.dto.command;

import lombok.Builder;
import ru.kubsu.borshchevyk.user.domain.model.privacy.Visibility;

/**
 * Command for updating a user's privacy settings.
 *
 * @param userId                 the unique identifier of the user
 * @param emailVisibility        the visibility setting for the user's email
 * @param searchByEmailVisibility the visibility setting for finding the user by email
 * @param profilePhotoVisibility the visibility setting for the user's profile photo
 * @param inviteToChatVisibility the visibility setting for inviting the user to chats
 * @author Aleksey Timko
 */
@Builder
public record UpdatePrivacySettingsCommand(
    String userId,
    Visibility emailVisibility,
    Visibility searchByEmailVisibility,
    Visibility profilePhotoVisibility,
    Visibility inviteToChatVisibility
) {
}
