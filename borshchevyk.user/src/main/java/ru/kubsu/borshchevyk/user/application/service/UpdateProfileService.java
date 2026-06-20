package ru.kubsu.borshchevyk.user.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.user.application.dto.command.UpdateProfileCommand;
import ru.kubsu.borshchevyk.user.application.port.in.UpdateProfileUseCase;
import ru.kubsu.borshchevyk.user.application.port.out.LoadUserPort;
import ru.kubsu.borshchevyk.user.application.port.out.SaveUserPort;
import ru.kubsu.borshchevyk.user.application.port.out.UserEventPublisherPort;
import ru.kubsu.borshchevyk.user.domain.event.UserUpdatedEvent;
import ru.kubsu.borshchevyk.user.domain.exception.UserNotFoundException;
import ru.kubsu.borshchevyk.user.domain.model.user.User;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

import java.util.UUID;

/**
 * Service for updating user profiles.
 *
 * @author Aleksey Timko
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateProfileService implements UpdateProfileUseCase {
    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final UserEventPublisherPort userEventPublisherPort;

    @Override
    public User updateProfile(UpdateProfileCommand command) {
        UserId userId = new UserId(UUID.fromString(command.userId()));
        User user = loadUserPort.loadUserById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (command.firstName() != null) user.setFirstName(command.firstName());
        if (command.lastName() != null) user.setLastName(command.lastName());
        if (command.bio() != null) user.setBio(command.bio());
        if (command.avatarUrl() != null) {
            String newAvatarUrl = command.avatarUrl();
            user.setAvatarUrl(newAvatarUrl);
            if (user.getAvatars() == null) {
                user.setAvatars(new java.util.ArrayList<>());
            }
            if (!newAvatarUrl.isEmpty() && !user.getAvatars().contains(newAvatarUrl)) {
                user.getAvatars().add(newAvatarUrl);
            }
        }

        saveUserPort.saveUser(user);

        if (!command.isSyncMutation()) {
            userEventPublisherPort.publishUpdated(UserUpdatedEvent.builder()
                    .userId(userId.getValue())
                    .build());
        }

        return user;
    }
}

