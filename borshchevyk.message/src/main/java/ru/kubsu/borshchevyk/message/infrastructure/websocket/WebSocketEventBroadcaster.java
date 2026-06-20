package ru.kubsu.borshchevyk.message.infrastructure.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatMembersPort;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.AppEventDto;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventBroadcaster {

    private final SimpMessagingTemplate messagingTemplate;
    private final LoadChatMembersPort loadChatMembersPort;

    public void broadcastToUser(UUID userId, String eventType, Object payload) {
        log.debug("Broadcasting event {} to user {}", eventType, userId);
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/events",
                new AppEventDto(eventType, payload)
        );
    }

    public void broadcastToChatMembers(UUID chatId, String eventType, Object payload) {
        log.debug("Broadcasting event {} to members of chat {}", eventType, chatId);
        List<ChatMember> members = loadChatMembersPort.findByChatId(new ChatId(chatId));
        for (ChatMember member : members) {
            broadcastToUser(member.getUserId().value(), eventType, payload);
        }
    }
    
    public void broadcastToAll(String eventType, Object payload) {
        log.debug("Broadcasting event {} to all users", eventType);
        messagingTemplate.convertAndSend(
                "/topic/events",
                new AppEventDto(eventType, payload)
        );
    }
}
