package ru.kubsu.borshchevyk.user.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.user.application.port.in.DeleteUserUseCase;
import ru.kubsu.borshchevyk.user.application.port.out.DeleteUserPort;
import ru.kubsu.borshchevyk.user.application.port.out.UserEventPublisherPort;
import ru.kubsu.borshchevyk.user.domain.event.UserDeletedEvent;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

import java.util.UUID;

/**
 * Service for deleting user profiles.
 * Removes user from the database and publishes a deletion event.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteUserService implements DeleteUserUseCase {

    private final DeleteUserPort deleteUserPort;
    private final UserEventPublisherPort userEventPublisherPort;

    /**
     * Deletes a user profile by ID.
     *
     * @param userId UUID of the user to delete
     */
    @Override
    public void deleteUser(String userId, boolean isSyncMutation) {
        log.info("Deleting user profile with ID: {}", userId);

        UserId id = new UserId(UUID.fromString(userId));
        deleteUserPort.deleteUser(id);

        if (!isSyncMutation) {
            userEventPublisherPort.publishDeleted(UserDeletedEvent.builder()
                    .userId(id.getValue())
                    .build());
        }

        log.info("Successfully deleted user for ID: {} (isSyncMutation: {})", userId, isSyncMutation);
    }}
