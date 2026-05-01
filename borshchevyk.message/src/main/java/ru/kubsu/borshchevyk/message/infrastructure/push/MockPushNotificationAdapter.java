package ru.kubsu.borshchevyk.message.infrastructure.push;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.PushNotificationPort;
import ru.kubsu.borshchevyk.message.domain.model.notification.AppNotification;

/**
 * Mock implementation of PushNotificationPort.
 *
 * @author Aleksey Timko
 */
@Slf4j
@Component
public class MockPushNotificationAdapter implements PushNotificationPort {

    @Override
    public void sendPushNotification(AppNotification notification) {
        // Here we would typically integrate with Firebase Cloud Messaging (FCM) or Apple Push Notification service (APNs).
        log.info(">>> PUSH NOTIFICATION SENT TO USER {} <<<", notification.getUserId().value());
        log.info("Title: {}", notification.getTitle());
        log.info("Body: {}", notification.getBody());
    }
}
