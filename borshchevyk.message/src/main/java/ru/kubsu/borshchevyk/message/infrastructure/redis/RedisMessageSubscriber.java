package ru.kubsu.borshchevyk.message.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
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
        } catch (Exception e) {
            log.error("Failed to process message from Redis topic ws.messages", e);
        }
    }
}