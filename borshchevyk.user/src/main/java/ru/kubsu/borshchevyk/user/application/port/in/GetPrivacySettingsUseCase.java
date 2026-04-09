package ru.kubsu.borshchevyk.user.application.port.in;

import ru.kubsu.borshchevyk.user.domain.model.privacy.PrivacySettings;

public interface GetPrivacySettingsUseCase {
    PrivacySettings getPrivacySettings(String userId);
}
