package ru.kubsu.borshchevyk.user.application.port.in;

import ru.kubsu.borshchevyk.user.domain.event.UserRegisteredEvent;

public interface CreateUserUseCase {
    void createUser(UserRegisteredEvent event);
}
