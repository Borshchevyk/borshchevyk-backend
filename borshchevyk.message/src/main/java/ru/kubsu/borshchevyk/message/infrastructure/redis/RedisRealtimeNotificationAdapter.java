package ru.kubsu.borshchevyk.message.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.RealtimeNotificationPort;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortUserDto;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortChatDto;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade.UserEnrichmentService;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade.ChatEnrichmentService;
import ru.kubsu.borshchevyk.message.application.port.out.ChatPort;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade.ChatFacade;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ChatResponse;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Aleksey Timko
 * @since 2026-05-01
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RedisRealtimeNotificationAdapter implements RealtimeNotificationPort {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final UserEnrichmentService userEnrichmentService;
    private final ChatEnrichmentService chatEnrichmentService;
    private final ChatPort chatPort;
    private final ChatFacade chatFacade;

    @Override
    public void notifyUser(UserId userId, Message message) {
        try {
            ShortUserDto author = message.getAuthorId() != null ? userEnrichmentService.enrichUser(message.getAuthorId().value()) : null;
            ShortChatDto chat = message.getChatId() != null ? chatEnrichmentService.enrichChat(message.getChatId().value(), userId.value()) : null;
            ShortChatDto fwChat = message.getForwardedFromChatId() != null ? chatEnrichmentService.enrichChat(message.getForwardedFromChatId().value(), userId.value()) : null;
            ShortUserDto fwUser = message.getForwardedFromUserId() != null ? userEnrichmentService.enrichUser(message.getForwardedFromUserId().value()) : null;

            List<NotificationDto.AttachmentDto> attachmentDtos = message.getAttachments() != null ?
                    message.getAttachments().stream()
                            .map(a -> new NotificationDto.AttachmentDto(
                                    a.getId() != null ? a.getId().toString() : null, 
                                    a.getType(),
                                    a.getOriginalFilename(),
                                    a.getExtension(),
                                    a.getSizeBytes(),
                                    a.getDuration()
                            ))
                            .collect(Collectors.toList()) : null;

            NotificationDto.MessageDto messageDto = new NotificationDto.MessageDto(
                message.getId() != null ? message.getId().value().toString() : null,
                chat,
                author,
                message.getText(),
                message.getCreatedAt() != null ? message.getCreatedAt().toString() : null,
                message.getUpdatedAt() != null ? message.getUpdatedAt().toString() : null,
                message.isDeleted(),
                message.getStatus() != null ? message.getStatus().name() : null,
                fwChat,
                fwUser,
                attachmentDtos
            );

            NotificationDto notification = new NotificationDto(userId.value().toString(), messageDto, null, null);
            
            String json = objectMapper.writeValueAsString(notification);
            redisTemplate.convertAndSend("ws.messages", json);
            
        } catch (Exception e) {
            log.error("Failed to publish notification for user {} to Redis", userId.value(), e);
        }
    }

    @Override
    public void notifyChatEvent(UserId userId, ru.kubsu.borshchevyk.message.domain.model.value.ChatId chatId, String action) {
        try {
            Chat chat = chatPort.findById(chatId).orElse(null);
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