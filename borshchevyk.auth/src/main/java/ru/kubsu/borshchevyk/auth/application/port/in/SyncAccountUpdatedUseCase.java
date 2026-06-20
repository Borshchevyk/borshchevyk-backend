package ru.kubsu.borshchevyk.auth.application.port.in;

import ru.kubsu.borshchevyk.auth.domain.event.UserUpdatedEvent;

/**
 * Use case for synchronizing account data based on user events.
 */
public interface SyncAccountUpdatedUseCase {
    /**
     * Synchronizes account data when a user is updated.
     *
     * @param event the user updated event
     */
    void syncUpdated(UserUpdatedEvent event);
}
