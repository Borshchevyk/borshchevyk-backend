package ru.kubsu.borshchevyk.message.infrastructure.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import ru.kubsu.borshchevyk.message.infrastructure.websocket.WebSocketEventBroadcaster;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.PublishChatEventPort;
import ru.kubsu.borshchevyk.message.domain.event.chat.ChatCreatedEvent;
import ru.kubsu.borshchevyk.message.domain.event.chat.ChatMemberEvent;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade.ChatEnrichmentService;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade.UserEnrichmentService;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatNotificationListener {

    private final WebSocketEventBroadcaster eventBroadcaster;
    private final PublishChatEventPort publishChatEventPort;
    private final ChatEnrichmentService chatEnrichmentService;
    private final UserEnrichmentService userEnrichmentService;

    @Async
    @EventListener
    public void handleChatCreatedEvent(ChatCreatedEvent event) {
        log.info("Handling ChatCreatedEvent for chat: {}", event.getChatId());
        
        if (event.getInitialMemberIds() != null) {
            for (UUID memberId : event.getInitialMemberIds()) {
                sendJoinNotification(event.getChatId(), event.getCreatorId(), memberId);
            }
        }
    }

    private void sendJoinNotification(UUID chatId, UUID creatorId, UUID memberId) {
        try {
            eventBroadcaster.broadcastToUser(
                    memberId,
                    "MEMBER_ADDED",
                    new ChatMemberEvent(
                            chatEnrichmentService.enrichChat(chatId, creatorId),
                            userEnrichmentService.enrichUser(memberId),
                            "JOIN"
                    )
            );

            // Kafka Event
            publishChatEventPort.publishChatEvent(
                    new UserId(memberId),
                    new ChatId(chatId),
                    "JOINED"
            );
        } catch (Exception e) {
            log.error("Failed to send join notification for chat {} and member {}: {}", chatId, memberId, e.getMessage());
        }
    }
}