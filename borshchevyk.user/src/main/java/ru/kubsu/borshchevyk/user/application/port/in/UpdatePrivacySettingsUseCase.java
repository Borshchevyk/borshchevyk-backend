package ru.kubsu.borshchevyk.user.application.port.in;

import ru.kubsu.borshchevyk.user.application.dto.command.UpdatePrivacySettingsCommand;
import ru.kubsu.borshchevyk.user.domain.model.privacy.PrivacySettings;

/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
public interface UpdatePrivacySettingsUseCase {
    PrivacySettings updatePrivacySettings(UpdatePrivacySettingsCommand command);
}

