package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.notification.AppNotification;

/**
 * Output port for sending push notifications.
 *
 * @author Aleksey Timko
 */
public interface PushNotificationPort {
    void sendPushNotification(AppNotification notification);
}
