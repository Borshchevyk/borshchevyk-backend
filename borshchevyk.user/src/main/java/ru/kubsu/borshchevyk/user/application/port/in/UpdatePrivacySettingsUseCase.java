package ru.kubsu.borshchevyk.user.application.port.in;

import ru.kubsu.borshchevyk.user.application.dto.command.UpdatePrivacySettingsCommand;
import ru.kubsu.borshchevyk.user.domain.model.privacy.PrivacySettings;

/**
 * UseCase for updating a user's privacy settings.
 *
 * @author Aleksey Timko
 */
public interface UpdatePrivacySettingsUseCase {
    
    /**
     * Updates privacy settings based on the provided command.
     *
     * @param command the command containing new privacy settings
     * @return the updated privacy settings
     */
    PrivacySettings updatePrivacySettings(UpdatePrivacySettingsCommand command);
}

