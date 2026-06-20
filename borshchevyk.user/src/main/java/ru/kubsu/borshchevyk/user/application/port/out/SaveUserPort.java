package ru.kubsu.borshchevyk.user.application.port.out;

import ru.kubsu.borshchevyk.user.domain.model.user.User;

/**
 * Outbound port for saving user data to the persistent store.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
public interface SaveUserPort {
    /**
     * Saves or updates a user.
     *
     * @param user the user domain model to save
     */
    void saveUser(User user);
}
