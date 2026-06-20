package ru.kubsu.borshchevyk.message.infrastructure.redis.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import ru.kubsu.borshchevyk.message.infrastructure.websocket.WebSocketEventBroadcaster;
import java.util.UUID;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.infrastructure.redis.dto.NotificationDto;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final WebSocketEventBroadcaster eventBroadcaster;

    @Override
    public void onMessage(@NonNull Message message, byte[] pattern) {
        try {
            String jsonStr = new String(message.getBody(), StandardCharsets.UTF_8);
            NotificationDto notification = objectMapper.readValue(jsonStr, NotificationDto.class);

            if (notification.getMessage() != null) {
                eventBroadcaster.broadcastToUser(
                        UUID.fromString(notification.getTargetUserId()),
                        notification.getMessage().isDeleted() ? "MESSAGE_DELETED" : "MESSAGE_CREATED",
                        notification.getMessage()
                );
            }

            if (notification.getChatEvent() != null) {
                eventBroadcaster.broadcastToUser(
                        UUID.fromString(notification.getTargetUserId()),
                        "CHAT_EVENT",
                        notification.getChatEvent()
                );
            }

            if (notification.getCallEvent() != null) {
                eventBroadcaster.broadcastToUser(
                        UUID.fromString(notification.getTargetUserId()),
                        "CALL_EVENT",
                        notification.getCallEvent()
                );
            }
        } catch (Exception e) {
            log.error("Failed to process message from Redis topic ws.messages", e);
        }
    }
}
