package ru.kubsu.borshchevyk.user.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.user.application.dto.command.EditUserCommand;
import ru.kubsu.borshchevyk.user.application.port.in.EditUserUseCase;
import ru.kubsu.borshchevyk.user.application.port.out.LoadUserPort;
import ru.kubsu.borshchevyk.user.application.port.out.SaveUserPort;
import ru.kubsu.borshchevyk.user.application.port.out.UserEventPublisherPort;
import ru.kubsu.borshchevyk.user.domain.event.UserUpdatedEvent;
import ru.kubsu.borshchevyk.user.domain.exception.UserNotFoundException;
import ru.kubsu.borshchevyk.user.domain.model.result.EditUserResult;
import ru.kubsu.borshchevyk.user.domain.model.user.User;
import ru.kubsu.borshchevyk.user.domain.model.value.Email;
import ru.kubsu.borshchevyk.user.domain.model.value.Tag;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

import java.util.UUID;

/**
 * Service for updating user profiles.
 * Handles partial updates and publishes update events.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EditUserService implements EditUserUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final UserEventPublisherPort userEventPublisherPort;

    /**
     * Updates user profile data.
     *
     * @param command Command containing user ID and fields to update
     * @return Result object with updated profile data
     * @throws UserNotFoundException if user with given ID does not exist
     */
    @Override
    public EditUserResult editUser(EditUserCommand command) {
        log.info("Updating user profile for ID: {}", command.userId());
        
        UserId userId = new UserId(UUID.fromString(command.userId()));
        User user = loadUserPort.loadUserById(userId)
                .orElseThrow(() -> {
                    log.error("User with ID {} not found for update", command.userId());
                    return new UserNotFoundException();
                });

        boolean updated = false;
        if (command.email() != null && !command.email().isBlank()) {
            log.debug("Updating email for user {}", command.userId());
            user.setEmail(new Email(command.email()));
            updated = true;
        }
        if (command.tag() != null && !command.tag().isBlank()) {
            log.debug("Updating tag for user {}", command.userId());
            user.setTag(new Tag(command.tag()));
            updated = true;
        }
        saveUserPort.saveUser(user);

        userEventPublisherPort.publishUpdated(UserUpdatedEvent.builder()
                .userId(userId.getValue())
                .email(user.getEmail() != null ? user.getEmail().getValue() : null)
                .tag(user.getTag() != null ? user.getTag().getValue() : null)
                .build());

        log.info("Successfully updated user profile for ID: {}", command.userId());
        
        return EditUserResult.builder()
                .userId(user.getUserId().getValue().toString())
                .email(user.getEmail() != null ? user.getEmail().getValue() : null)
                .tag(user.getTag() != null ? user.getTag().getValue() : null)
                .build();
    }
}
