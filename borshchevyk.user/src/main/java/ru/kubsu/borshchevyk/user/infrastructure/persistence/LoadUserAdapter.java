package ru.kubsu.borshchevyk.user.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.user.application.port.out.LoadUserPort;
import ru.kubsu.borshchevyk.user.domain.model.user.User;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;
import ru.kubsu.borshchevyk.user.infrastructure.persistence.mapper.UserPersistenceMapper;
import ru.kubsu.borshchevyk.user.infrastructure.persistence.repository.UserSpringRepository;

import java.util.Optional;

/**
 * Persistence adapter for loading user data from the database.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoadUserAdapter implements LoadUserPort {

    private final UserSpringRepository userRepository;
    private final UserPersistenceMapper userMapper;

    /**
     * Loads a user by their unique identifier from the database.
     *
     * @param userId the unique identifier of the user
     * @return an Optional containing the user if found, or empty otherwise
     */
    @Override
    public Optional<User> loadUserById(UserId userId) {
        log.debug("Loading user with ID: {}", userId);
        return userRepository.findById(userId.getValue())
                .map(userMapper::toDomain);
    }
}
