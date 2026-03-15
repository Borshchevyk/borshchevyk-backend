package ru.kubsu.borshchevyk.auth.application.port.out;

import ru.kubsu.borshchevyk.auth.domain.event.UserRegisteredEvent;

/**
 * Port for publishing user registration events.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
public interface UserRegisteredEventPublisherPort {
    /**
     * Publishes a user registered event.
     *
     * @param event the user registration event
     */
    void publish(UserRegisteredEvent event);
}
