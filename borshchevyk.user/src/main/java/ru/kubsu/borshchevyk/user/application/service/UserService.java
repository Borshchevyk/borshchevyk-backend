package ru.kubsu.borshchevyk.user.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.user.application.dto.command.GetUserProfileCommand;
import ru.kubsu.borshchevyk.user.application.dto.command.SearchUsersCommand;
import ru.kubsu.borshchevyk.user.application.dto.command.UpdateProfileCommand;
import ru.kubsu.borshchevyk.user.application.port.in.GetUserProfileUseCase;
import ru.kubsu.borshchevyk.user.application.port.in.SearchUsersUseCase;
import ru.kubsu.borshchevyk.user.application.port.in.UpdateProfileUseCase;
import ru.kubsu.borshchevyk.user.application.port.out.ContactPort;
import ru.kubsu.borshchevyk.user.application.port.out.LoadUserPort;
import ru.kubsu.borshchevyk.user.application.port.out.PrivacySettingsPort;
import ru.kubsu.borshchevyk.user.application.port.out.SaveUserPort;
import ru.kubsu.borshchevyk.user.domain.exception.UserNotFoundException;
import ru.kubsu.borshchevyk.user.domain.model.privacy.PrivacySettings;
import ru.kubsu.borshchevyk.user.domain.model.privacy.Visibility;
import ru.kubsu.borshchevyk.user.domain.model.user.User;
import ru.kubsu.borshchevyk.user.domain.model.value.Tag;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UpdateProfileUseCase, SearchUsersUseCase, GetUserProfileUseCase {
    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final PrivacySettingsPort privacySettingsPort;
    private final ContactPort contactPort;

    @Override
    public User updateProfile(UpdateProfileCommand command) {
        UserId userId = new UserId(UUID.fromString(command.userId()));
        User user = loadUserPort.loadUserById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (command.firstName() != null) user.setFirstName(command.firstName());
        if (command.lastName() != null) user.setLastName(command.lastName());
        if (command.bio() != null) user.setBio(command.bio());
        if (command.avatarUrl() != null) user.setAvatarUrl(command.avatarUrl());

        saveUserPort.saveUser(user);
        return user;
    }

    @Override
    public List<User> searchUsers(SearchUsersCommand command) {
        List<User> users = loadUserPort.searchUsers(command.query());
        UserId requesterId = command.requesterId() != null && !command.requesterId().isBlank() ? new UserId(UUID.fromString(command.requesterId())) : null;

        boolean isEmailSearch = command.query() != null && command.query().contains("@");

        return users.stream()
                .filter(user -> {
                    if (isEmailSearch) {
                        PrivacySettings settings = getPrivacySettings(user.getUserId());
                        if (settings.getSearchByEmailVisibility() != Visibility.EVERYONE) {
                            return false;
                        }
                    }
                    return true;
                })
                .map(user -> applyPrivacy(user, requesterId))
                .collect(Collectors.toList());
    }

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
