package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.notification.AppNotification;

public interface SaveNotificationPort {
    AppNotification save(AppNotification notification);
}