package ru.kubsu.borshchevyk.message.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatPort;
import ru.kubsu.borshchevyk.message.application.port.out.NotifyChatEventPort;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ChatResponse;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade.ChatFacade;
import ru.kubsu.borshchevyk.message.infrastructure.redis.dto.NotificationDto;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RedisNotifyChatEventAdapter implements NotifyChatEventPort {
    private final LoadChatPort loadChatPort;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final ChatFacade chatFacade;

    @Override
    public void notifyChatEvent(UserId userId, ChatId chatId, String action) {
        try {
            Chat chat = loadChatPort.findById(chatId).orElse(null);
            ChatResponse chatResponse = chat != null ? chatFacade.enrichChatResponse(chat, userId.value()) : null;
            
            NotificationDto.ChatEventDto eventDto = new NotificationDto.ChatEventDto(chatResponse, action);
            NotificationDto notification = new NotificationDto(userId.value().toString(), null, eventDto, null);

            String json = objectMapper.writeValueAsString(notification);
            redisTemplate.convertAndSend("ws.messages", json);

        } catch (Exception e) {
            log.error("Failed to publish chat event for user {} to Redis", userId.value(), e);
        }
    }
}
