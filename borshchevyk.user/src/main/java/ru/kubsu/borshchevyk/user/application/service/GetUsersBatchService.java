package ru.kubsu.borshchevyk.user.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.user.application.dto.command.GetUsersBatchCommand;
import ru.kubsu.borshchevyk.user.application.port.in.GetUsersBatchUseCase;
import ru.kubsu.borshchevyk.user.application.port.out.ContactPort;
import ru.kubsu.borshchevyk.user.application.port.out.LoadUserPort;
import ru.kubsu.borshchevyk.user.application.port.out.PrivacySettingsPort;
import ru.kubsu.borshchevyk.user.domain.model.privacy.PrivacySettings;
import ru.kubsu.borshchevyk.user.domain.model.privacy.Visibility;
import ru.kubsu.borshchevyk.user.domain.model.user.User;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetUsersBatchService implements GetUsersBatchUseCase {
    private final LoadUserPort loadUserPort;
    private final PrivacySettingsPort privacySettingsPort;
    private final ContactPort contactPort;

    @Override
    public List<User> getUsersBatch(GetUsersBatchCommand command) {
        List<UserId> ids = command.userIds().stream().map(UserId::new).collect(Collectors.toList());
        List<User> users = loadUserPort.loadUsersByIds(ids);
        
        UserId requesterId = command.requesterId() != null && !command.requesterId().isBlank() ? new UserId(UUID.fromString(command.requesterId())) : null;

        return users.stream()
                .map(user -> applyPrivacy(user, requesterId))
                .collect(Collectors.toList());
    }

    private PrivacySettings getPrivacySettings(UserId userId) {
        return privacySettingsPort.loadByUserId(userId)
                .orElseGet(() -> PrivacySettings.builder().userId(userId).build());
    }

    private User applyPrivacy(User user, UserId requesterId) {
        PrivacySettings settings = getPrivacySettings(user.getUserId());

        if (!canSeeEmail(user.getUserId(), requesterId, settings.getEmailVisibility())) {
            user.setEmail(null);
        }

        return user;
    }

    private boolean canSeeEmail(UserId targetId, UserId requesterId, Visibility visibility) {
        if (visibility == Visibility.EVERYONE) return true;
        if (visibility == Visibility.NOBODY) return false;
        if (visibility == Visibility.CONTACTS && requesterId != null) {
            return contactPort.isContact(requesterId, targetId);
        }
        return false;
    }
}
