package ru.kubsu.borshchevyk.auth.application.port.out;

import ru.kubsu.borshchevyk.auth.domain.event.UserRegisteredEvent;

public interface UserRegisteredEventPublisherPort {
    void publish(UserRegisteredEvent event);
}
