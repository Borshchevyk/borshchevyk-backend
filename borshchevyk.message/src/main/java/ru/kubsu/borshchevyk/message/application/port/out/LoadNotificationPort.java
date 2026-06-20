package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.notification.AppNotification;

import java.util.Optional;
import java.util.UUID;

public interface LoadNotificationPort {
    Optional<AppNotification> findById(UUID id);
}