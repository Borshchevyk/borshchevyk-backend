package ru.kubsu.borshchevyk.user.application.port.out;

import ru.kubsu.borshchevyk.user.domain.model.user.User;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

import java.util.Optional;

/**
 * Port for loading user data.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
public interface LoadUserPort {
    /**
     * Loads a user by their identifier.
     *
     * @param userId the identifier of the user to load
     * @return an Optional containing the user if found, or empty otherwise
     */
    Optional<User> loadUserById(UserId userId);
    
    Optional<User> loadUserByTag(ru.kubsu.borshchevyk.user.domain.model.value.Tag tag);
    
    java.util.List<User> searchUsers(String query);
}
