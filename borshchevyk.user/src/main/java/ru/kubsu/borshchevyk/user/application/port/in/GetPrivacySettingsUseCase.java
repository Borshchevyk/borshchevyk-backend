package ru.kubsu.borshchevyk.user.application.port.in;

import ru.kubsu.borshchevyk.user.domain.model.privacy.PrivacySettings;

/**
 * Port for retrieving privacy settings of a user.
 *
 * @author Aleksey Timko
 */
public interface GetPrivacySettingsUseCase {

    /**
     * Retrieves the privacy settings for a given user.
     *
     * @param userId the unique identifier of the user
     * @return the privacy settings of the user
     */
    PrivacySettings getPrivacySettings(String userId);
}

