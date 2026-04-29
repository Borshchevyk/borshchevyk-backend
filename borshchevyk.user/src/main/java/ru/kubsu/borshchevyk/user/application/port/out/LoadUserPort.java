package ru.kubsu.borshchevyk.user.application.port.out;

import ru.kubsu.borshchevyk.user.domain.model.user.User;
import ru.kubsu.borshchevyk.user.domain.model.value.Tag;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

import java.util.List;
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
    
    /**
     * Loads a user by their tag.
     *
     * @param tag the tag of the user to load
     * @return an Optional containing the user if found, or empty otherwise
     */
    Optional<User> loadUserByTag(Tag tag);
    
    /**
     * Searches for users using a query string.
     *
     * @param query the search query
     * @return a list of matching users
     */
    List<User> searchUsers(String query);
    
    /**
     * Loads a batch of users by their identifiers.
     *
     * @param userIds the list of user identifiers
     * @return a list of loaded users
     */
    List<User> loadUsersByIds(List<UserId> userIds);
}
