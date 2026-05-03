package ru.kubsu.borshchevyk.auth.application.port.in;

import ru.kubsu.borshchevyk.auth.domain.event.UserDeletedEvent;
import ru.kubsu.borshchevyk.auth.domain.event.UserUpdatedEvent;

/**
 * Use case for synchronizing account data based on user events.
 */
public interface SyncAccountUseCase {
    /**
     * Synchronizes account data when a user is updated.
     *
     * @param event the user updated event
     */
    void syncUpdated(UserUpdatedEvent event);

    /**
     * Synchronizes account data when a user is deleted.
     *
     * @param event the user deleted event
     */
    void syncDeleted(UserDeletedEvent event);
}
