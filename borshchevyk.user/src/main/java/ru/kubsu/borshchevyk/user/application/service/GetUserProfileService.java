package ru.kubsu.borshchevyk.user.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.user.application.dto.command.GetUserProfileCommand;
import ru.kubsu.borshchevyk.user.application.port.in.GetUserProfileUseCase;
import ru.kubsu.borshchevyk.user.application.port.out.ContactPort;
import ru.kubsu.borshchevyk.user.application.port.out.LoadUserPort;
import ru.kubsu.borshchevyk.user.application.port.out.PrivacySettingsPort;
import ru.kubsu.borshchevyk.user.domain.exception.UserNotFoundException;
import ru.kubsu.borshchevyk.user.domain.model.privacy.PrivacySettings;
import ru.kubsu.borshchevyk.user.domain.model.privacy.Visibility;
import ru.kubsu.borshchevyk.user.domain.model.user.User;
import ru.kubsu.borshchevyk.user.domain.model.value.Tag;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

import java.util.UUID;

/**
 * Service for retrieving user profiles with privacy settings applied.
 *
 * @author Aleksey Timko
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetUserProfileService implements GetUserProfileUseCase {
    private final LoadUserPort loadUserPort;
    private final PrivacySettingsPort privacySettingsPort;
    private final ContactPort contactPort;

    @Override
    public User getUserProfile(GetUserProfileCommand command) {
        User user;
        if (command.targetUserIdOrTag().startsWith("@")) {
            user = loadUserPort.loadUserByTag(new Tag(command.targetUserIdOrTag().substring(1)))
                    .orElseThrow(UserNotFoundException::new);
        } else {
            try {
                user = loadUserPort.loadUserById(new UserId(UUID.fromString(command.targetUserIdOrTag())))
                        .orElseThrow(UserNotFoundException::new);
            } catch (IllegalArgumentException e) {
                user = loadUserPort.loadUserByTag(new Tag(command.targetUserIdOrTag()))
                        .orElseThrow(UserNotFoundException::new);
            }
        }

        UserId requesterId = command.requesterId() != null && !command.requesterId().isBlank() ? new UserId(UUID.fromString(command.requesterId())) : null;
        return applyPrivacy(user, requesterId);
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

