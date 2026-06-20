package ru.kubsu.borshchevyk.user.application.port.in;

import ru.kubsu.borshchevyk.user.domain.event.UserRegisteredEvent;

/**
 * Port for creating a user in response to registration.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
public interface CreateUserUseCase {
    /**
     * Creates a user based on the registration event.
     *
     * @param event the user registration event
     */
    void createUser(UserRegisteredEvent event);
}
