package ru.kubsu.borshchevyk.user.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.user.application.dto.command.SearchUsersCommand;
import ru.kubsu.borshchevyk.user.application.port.in.SearchUsersUseCase;
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
public class SearchUsersService implements SearchUsersUseCase {
    private final LoadUserPort loadUserPort;
    private final PrivacySettingsPort privacySettingsPort;
    private final ContactPort contactPort;

    @Override
    public List<User> searchUsers(SearchUsersCommand command) {
        List<User> users = loadUserPort.searchUsers(command.query());
        UserId requesterId = command.requesterId() != null && !command.requesterId().isBlank() ? new UserId(UUID.fromString(command.requesterId())) : null;

        boolean isEmailSearch = command.query() != null && command.query().contains("@");

        return users.stream()
                .filter(user -> {
                    if (requesterId != null && requesterId.equals(user.getUserId())) {
                        return false;
                    }
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

