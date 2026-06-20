package ru.kubsu.borshchevyk.user.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.user.application.port.out.LoadUserPort;
import ru.kubsu.borshchevyk.user.domain.model.user.User;
import ru.kubsu.borshchevyk.user.domain.model.value.Tag;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;
import ru.kubsu.borshchevyk.user.infrastructure.persistence.mapper.UserPersistenceMapper;
import ru.kubsu.borshchevyk.user.infrastructure.persistence.repository.UserSpringRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    /**
     * Loads a user by their unique tag from the database.
     *
     * @param tag the unique tag of the user
     * @return an Optional containing the user if found, or empty otherwise
     */
    @Override
    public Optional<User> loadUserByTag(Tag tag) {
        log.debug("Loading user with tag: {}", tag.getValue());
        return userRepository.findByTag(tag.getValue())
                .map(userMapper::toDomain);
    }

    /**
     * Searches for users based on a string query.
     *
     * @param query the search query
     * @return a list of users matching the query
     */
    @Override
    public List<User> searchUsers(String query) {
        log.debug("Searching users with query: {}", query);
        return userRepository.search(query).stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Loads a list of users by their unique identifiers from the database.
     *
     * @param userIds the list of unique identifiers of the users
     * @return a list of users
     */
    @Override
    public List<User> loadUsersByIds(List<UserId> userIds) {
        log.debug("Loading {} users by IDs", userIds.size());
        List<UUID> ids = userIds.stream()
                .map(UserId::getValue)
                .collect(Collectors.toList());
        return userRepository.findAllById(ids).stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
    }
}
