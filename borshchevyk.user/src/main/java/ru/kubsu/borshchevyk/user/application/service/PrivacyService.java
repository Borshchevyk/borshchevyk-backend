package ru.kubsu.borshchevyk.user.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.user.application.dto.command.UpdatePrivacySettingsCommand;
import ru.kubsu.borshchevyk.user.application.port.in.UpdatePrivacySettingsUseCase;
import ru.kubsu.borshchevyk.user.application.port.in.GetPrivacySettingsUseCase;
import ru.kubsu.borshchevyk.user.application.port.out.PrivacySettingsPort;
import ru.kubsu.borshchevyk.user.domain.model.privacy.PrivacySettings;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

import java.util.UUID;

/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
@Slf4j
/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
@Service
/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
@RequiredArgsConstructor
/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
public class PrivacyService implements UpdatePrivacySettingsUseCase, GetPrivacySettingsUseCase {
    private final PrivacySettingsPort privacySettingsPort;

    @Override
    public PrivacySettings getPrivacySettings(String userId) {
        UserId id = new UserId(UUID.fromString(userId));
        return privacySettingsPort.loadByUserId(id)
                .orElseGet(() -> PrivacySettings.builder().userId(id).build());
    }

    @Override
    public PrivacySettings updatePrivacySettings(UpdatePrivacySettingsCommand command) {
        UserId userId = new UserId(UUID.fromString(command.userId()));
        PrivacySettings settings = privacySettingsPort.loadByUserId(userId)
                .orElseGet(() -> PrivacySettings.builder().userId(userId).build());

        if (command.emailVisibility() != null) settings.setEmailVisibility(command.emailVisibility());
        if (command.searchByEmailVisibility() != null) settings.setSearchByEmailVisibility(command.searchByEmailVisibility());
        if (command.profilePhotoVisibility() != null) settings.setProfilePhotoVisibility(command.profilePhotoVisibility());
        if (command.inviteToChatVisibility() != null) settings.setInviteToChatVisibility(command.inviteToChatVisibility());

        return privacySettingsPort.save(settings);
    }
}

