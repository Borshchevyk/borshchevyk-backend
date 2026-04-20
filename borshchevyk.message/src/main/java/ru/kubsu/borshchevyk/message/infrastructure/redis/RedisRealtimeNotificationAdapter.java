package ru.kubsu.borshchevyk.message.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.RealtimeNotificationPort;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisRealtimeNotificationAdapter implements RealtimeNotificationPort {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void notifyUser(UserId userId, Message message) {
        try {
            NotificationDto.MessageDto messageDto = NotificationDto.MessageDto.from(message);
            NotificationDto notification = new NotificationDto(userId.value().toString(), messageDto, null);
            
            String json = objectMapper.writeValueAsString(notification);
            redisTemplate.convertAndSend("ws.messages", json);
            
        } catch (Exception e) {
            log.error("Failed to publish notification for user {} to Redis", userId.value(), e);
        }
    }

    @Override
    public void notifyChatEvent(UserId userId, ru.kubsu.borshchevyk.message.domain.model.value.ChatId chatId, String action) {
        try {
            NotificationDto.ChatEventDto eventDto = new NotificationDto.ChatEventDto(chatId.value().toString(), action);
            NotificationDto notification = new NotificationDto(userId.value().toString(), null, eventDto);

            String json = objectMapper.writeValueAsString(notification);
            redisTemplate.convertAndSend("ws.messages", json);

        } catch (Exception e) {
            log.error("Failed to publish chat event for user {} to Redis", userId.value(), e);
        }
    }
}