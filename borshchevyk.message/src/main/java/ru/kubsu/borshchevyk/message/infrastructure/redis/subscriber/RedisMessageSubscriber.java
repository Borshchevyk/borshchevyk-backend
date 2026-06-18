package ru.kubsu.borshchevyk.message.infrastructure.redis.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.infrastructure.redis.dto.NotificationDto;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(@NonNull Message message, byte[] pattern) {
        try {
            String jsonStr = new String(message.getBody(), StandardCharsets.UTF_8);
            NotificationDto notification = objectMapper.readValue(jsonStr, NotificationDto.class);

            if (notification.getMessage() != null) {
                messagingTemplate.convertAndSendToUser(
                        notification.getTargetUserId(),
                        "/queue/messages",
                        notification.getMessage()
                );
            }

            if (notification.getChatEvent() != null) {
                messagingTemplate.convertAndSendToUser(
                        notification.getTargetUserId(),
                        "/queue/chats",
                        notification.getChatEvent()
                );
            }

            if (notification.getCallEvent() != null) {
                messagingTemplate.convertAndSendToUser(
                        notification.getTargetUserId(),
                        "/queue/calls",
                        notification.getCallEvent()
                );
            }
        } catch (Exception e) {
            log.error("Failed to process message from Redis topic ws.messages", e);
        }
    }
}
