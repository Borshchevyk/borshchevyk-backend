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
     * @param isSyncMutation indicates if this deletion was triggered by an offline sync
     */
    void deleteUser(String userId, boolean isSyncMutation);
    
    default void deleteUser(String userId) {
        deleteUser(userId, false);
    }
}
