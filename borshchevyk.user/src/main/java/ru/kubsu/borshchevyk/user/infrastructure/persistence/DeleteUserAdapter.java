package ru.kubsu.borshchevyk.user.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.user.application.port.out.DeleteUserPort;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;
import ru.kubsu.borshchevyk.user.infrastructure.persistence.repository.UserSpringRepository;

/**
 * Persistence adapter for deleting users from the database.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteUserAdapter implements DeleteUserPort {

    private final UserSpringRepository userRepository;

    /**
     * Deletes a user from the database by their unique identifier.
     *
     * @param userId the unique identifier of the user to delete
     */
    @Override
    public void deleteUser(UserId userId) {
        log.debug("Deleting user with ID: {}", userId);
        userRepository.deleteById(userId.getValue());
        log.info("Successfully deleted user with ID: {}", userId);
    }
}
