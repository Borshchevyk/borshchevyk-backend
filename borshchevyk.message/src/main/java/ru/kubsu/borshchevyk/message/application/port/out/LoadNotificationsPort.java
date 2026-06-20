package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.notification.AppNotification;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.util.List;

public interface LoadNotificationsPort {
    List<AppNotification> findByUserId(UserId userId, int limit);
}