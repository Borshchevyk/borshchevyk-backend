package ru.kubsu.borshchevyk.user.application.port.out;

import ru.kubsu.borshchevyk.user.domain.event.UserDeletedEvent;
import ru.kubsu.borshchevyk.user.domain.event.UserUpdatedEvent;

public interface UserEventPublisherPort {
    void publishUpdated(UserUpdatedEvent event);
    void publishDeleted(UserDeletedEvent event);
}
