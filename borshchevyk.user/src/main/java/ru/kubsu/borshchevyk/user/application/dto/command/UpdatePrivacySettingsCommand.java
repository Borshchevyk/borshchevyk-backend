package ru.kubsu.borshchevyk.user.application.dto.command;

import lombok.Builder;
import ru.kubsu.borshchevyk.user.domain.model.privacy.Visibility;

/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
public record UpdatePrivacySettingsCommand(
    String userId,
    Visibility emailVisibility,
    Visibility searchByEmailVisibility,
    Visibility profilePhotoVisibility,
    Visibility inviteToChatVisibility
) {
    @Builder
    public UpdatePrivacySettingsCommand {}
}

