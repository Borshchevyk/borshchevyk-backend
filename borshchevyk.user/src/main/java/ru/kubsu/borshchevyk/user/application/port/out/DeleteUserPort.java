package ru.kubsu.borshchevyk.user.application.port.out;

import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

/**
 * Port for deleting a user from the system.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
public interface DeleteUserPort {
    /**
     * Deletes a user by their identifier.
     *
     * @param userId the identifier of the user to delete
     */
    void deleteUser(UserId userId);
}
