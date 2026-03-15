package ru.kubsu.borshchevyk.user.application.port.in;

/**
 * Port for deleting a user.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
public interface DeleteUserUseCase {
    /**
     * Deletes a user by their identifier.
     *
     * @param userId the unique identifier of the user to delete
     */
    void deleteUser(String userId);
}
