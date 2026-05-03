package ru.kubsu.borshchevyk.auth.application.port.in;

import ru.kubsu.borshchevyk.auth.domain.event.UserDeletedEvent;

/**
 * Use case for synchronizing account data based on user events.
 */
public interface SyncAccountDeletedUseCase {
    /**
     * Synchronizes account data when a user is deleted.
     *
     * @param event the user deleted event
     */
    void syncDeleted(UserDeletedEvent event);
}
