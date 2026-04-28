package ru.kubsu.borshchevyk.user.application.port.out;

import ru.kubsu.borshchevyk.user.domain.model.privacy.PrivacySettings;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

import java.util.Optional;

/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
public interface PrivacySettingsPort {
    PrivacySettings save(PrivacySettings privacySettings);
    Optional<PrivacySettings> loadByUserId(UserId userId);
}

