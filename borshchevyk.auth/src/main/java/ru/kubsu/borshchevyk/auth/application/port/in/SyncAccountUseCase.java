package ru.kubsu.borshchevyk.auth.application.port.in;

import ru.kubsu.borshchevyk.auth.domain.event.UserDeletedEvent;
import ru.kubsu.borshchevyk.auth.domain.event.UserUpdatedEvent;

public interface SyncAccountUseCase {
    void syncUpdated(UserUpdatedEvent event);
    void syncDeleted(UserDeletedEvent event);
}
