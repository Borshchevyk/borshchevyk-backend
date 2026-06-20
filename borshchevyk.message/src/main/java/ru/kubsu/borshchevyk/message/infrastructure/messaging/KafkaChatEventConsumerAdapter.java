package ru.kubsu.borshchevyk.message.infrastructure.messaging;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.NotifyChatEventPort;
import ru.kubsu.borshchevyk.message.application.port.out.SaveNotificationPort;
import ru.kubsu.borshchevyk.message.application.port.out.SendPushNotificationPort;
import ru.kubsu.borshchevyk.message.domain.model.notification.AppNotification;
import ru.kubsu.borshchevyk.message.domain.model.notification.NotificationType;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaChatEventConsumerAdapter {

    private final NotifyChatEventPort notifyChatEventPort;
    private final SaveNotificationPort saveNotificationPort;

    private final ObjectMapper objectMapper;
    private final SendPushNotificationPort sendPushNotificationPort;

    @KafkaListener(topics = "chats.events", groupId = "${spring.kafka.consumer.group-id}")
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
                
                notifyChatEventPort.notifyChatEvent(userId, chatId, action);
                
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

                    saveNotificationPort.save(notification);
                    sendPushNotificationPort.sendPushNotification(notification);
                }
            }
        } catch (Exception e) {
            log.error("Error processing ChatEvent: {}", e.getMessage(), e);
        }
    }
}