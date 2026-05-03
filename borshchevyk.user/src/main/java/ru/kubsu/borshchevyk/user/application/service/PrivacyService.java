package ru.kubsu.borshchevyk.user.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.user.application.dto.command.CheckInvitePermissionCommand;
import ru.kubsu.borshchevyk.user.application.dto.command.UpdatePrivacySettingsCommand;
import ru.kubsu.borshchevyk.user.application.port.in.CheckInvitePermissionUseCase;
import ru.kubsu.borshchevyk.user.application.port.in.UpdatePrivacySettingsUseCase;
import ru.kubsu.borshchevyk.user.application.port.in.GetPrivacySettingsUseCase;
import ru.kubsu.borshchevyk.user.application.port.out.ContactPort;
import ru.kubsu.borshchevyk.user.application.port.out.PrivacySettingsPort;
import ru.kubsu.borshchevyk.user.domain.model.privacy.PrivacySettings;
import ru.kubsu.borshchevyk.user.domain.model.privacy.Visibility;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

import java.util.UUID;

/**
 * Service for managing user privacy settings.
 *
 * @author Aleksey Timko
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PrivacyService implements UpdatePrivacySettingsUseCase, GetPrivacySettingsUseCase, CheckInvitePermissionUseCase {
    private final PrivacySettingsPort privacySettingsPort;
    private final ContactPort contactPort;

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

    @Override
    public boolean checkInvitePermission(CheckInvitePermissionCommand command) {
        UserId targetId = new UserId(UUID.fromString(command.targetUserId()));
        UserId requesterId = new UserId(UUID.fromString(command.requesterId()));
        
        PrivacySettings settings = privacySettingsPort.loadByUserId(targetId)
                .orElseGet(() -> PrivacySettings.builder().userId(targetId).build());
                
        Visibility visibility = settings.getInviteToChatVisibility();
        
        if (visibility == Visibility.EVERYONE) return true;
        if (visibility == Visibility.NOBODY) return false;
        if (visibility == Visibility.CONTACTS) {
            return contactPort.isContact(requesterId, targetId);
        }
        return false;
    }
}

