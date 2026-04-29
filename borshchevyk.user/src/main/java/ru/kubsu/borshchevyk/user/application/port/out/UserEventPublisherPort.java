package ru.kubsu.borshchevyk.user.application.port.out;

import ru.kubsu.borshchevyk.user.domain.event.UserDeletedEvent;
import ru.kubsu.borshchevyk.user.domain.event.UserUpdatedEvent;

/**
 * Outbound port for publishing user-related domain events to the message broker.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
public interface UserEventPublisherPort {
    /**
     * Publishes a user updated event.
     *
     * @param event the user updated event
     */
    void publishUpdated(UserUpdatedEvent event);

    /**
     * Publishes a user deleted event.
     *
     * @param event the user deleted event
     */
    void publishDeleted(UserDeletedEvent event);
}
