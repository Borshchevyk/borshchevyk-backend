package ru.kubsu.borshchevyk.message.infrastructure.messaging;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.NotificationPort;
import ru.kubsu.borshchevyk.message.application.port.out.PushNotificationPort;
import ru.kubsu.borshchevyk.message.application.port.out.RealtimeNotificationPort;
import ru.kubsu.borshchevyk.message.domain.model.notification.AppNotification;
import ru.kubsu.borshchevyk.message.domain.model.notification.NotificationType;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Consumes chat events from Kafka and proxies them to the realtime notification port.
 *
 * @author Aleksey Timko
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaChatEventConsumerAdapter {

    private final ObjectMapper objectMapper;
    private final RealtimeNotificationPort realtimeNotificationPort;
    private final NotificationPort notificationPort;
    private final PushNotificationPort pushNotificationPort;

    @KafkaListener(topics = "chats.events", groupId = "${spring.kafka.consumer.group-id:message-service-group}")
    public void consumeChatEvent(String payload) {
        log.debug("Received ChatEvent payload: {}", payload);
        try {
            Map<String, String> event = objectMapper.readValue(payload, new TypeReference<Map<String, String>>() {});
            String userIdStr = event.get("userId");
            String chatIdStr = event.get("chatId");
            String action = event.get("action");

            if (userIdStr != null && chatIdStr != null && action != null) {
                UserId userId = new UserId(UUID.fromString(userIdStr));
                ChatId chatId = new ChatId(UUID.fromString(chatIdStr));
                
                // Proxy to Realtime WebSocket / Redis
                realtimeNotificationPort.notifyChatEvent(userId, chatId, action);
                
                // Save to Persistent Notification Center and Send Push
                if ("JOINED".equals(action)) {
                    AppNotification notification = AppNotification.builder()
                            .id(UUID.randomUUID())
                            .userId(userId)
                            .title("New Chat")
                            .body("You have been added to a new chat.")
                            .type(NotificationType.CHAT_INVITE)
                            .isRead(false)
                            .createdAt(LocalDateTime.now())
                            .build();

                    notificationPort.save(notification);
                    pushNotificationPort.sendPushNotification(notification);
                }
            }
        } catch (Exception e) {
            log.error("Error processing ChatEvent: {}", e.getMessage(), e);
        }
    }
}
