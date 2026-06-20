package ru.kubsu.borshchevyk.message.infrastructure.push;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.SendPushNotificationPort;
import ru.kubsu.borshchevyk.message.domain.model.notification.AppNotification;

/**
 * Mock implementation of SendPushNotificationPort.
 */
@Slf4j
@Component
public class MockSendPushNotificationAdapter implements SendPushNotificationPort {

    @Override
    public void sendPushNotification(AppNotification notification) {
        log.info(">>> PUSH NOTIFICATION SENT TO USER {} <<<", notification.getUserId().value());
        log.info("Title: {}", notification.getTitle());
        log.info("Body: {}", notification.getBody());
    }
}
