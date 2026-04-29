package ru.kubsu.borshchevyk.user.application.port.out;

import ru.kubsu.borshchevyk.user.domain.model.privacy.PrivacySettings;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

import java.util.Optional;

/**
 * Outbound port for managing user privacy settings in the persistent store.
 *
 * @author Aleksey Timko
 */
public interface PrivacySettingsPort {
    
    /**
     * Saves or updates privacy settings.
     *
     * @param privacySettings the settings to save
     * @return the saved privacy settings
     */
    PrivacySettings save(PrivacySettings privacySettings);
    
    /**
     * Loads privacy settings by user identifier.
     *
     * @param userId the user identifier
     * @return an Optional containing the privacy settings if found
     */
    Optional<PrivacySettings> loadByUserId(UserId userId);
}

