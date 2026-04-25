package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.kubsu.borshchevyk.message.infrastructure.websocket.UserPrincipal;
import ru.kubsu.borshchevyk.message.infrastructure.websocket.dto.TypingEvent;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade.UserEnrichmentService;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortUserDto;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserEnrichmentService userEnrichmentService;

    @MessageMapping("/chat/{chatId}/typing")
    public void handleTyping(
            @DestinationVariable UUID chatId,
            boolean isTyping,
            UserPrincipal principal) {
        
        UUID userId = UUID.fromString(principal.getName());
        log.debug("User {} is typing in chat {}: {}", userId, chatId, isTyping);

        ShortUserDto user = userEnrichmentService.enrichUser(userId);
        TypingEvent event = new TypingEvent(user, isTyping);
        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/typing", event);
    }
}
