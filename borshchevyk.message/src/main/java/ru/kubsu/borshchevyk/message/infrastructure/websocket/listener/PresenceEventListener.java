package ru.kubsu.borshchevyk.message.infrastructure.websocket.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import ru.kubsu.borshchevyk.message.infrastructure.websocket.WebSocketEventBroadcaster;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import ru.kubsu.borshchevyk.message.application.dto.response.PresenceStatusResponse;
import ru.kubsu.borshchevyk.message.domain.model.user.UserPrincipal;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatMembersPort;
import ru.kubsu.borshchevyk.message.application.port.out.LoadUserChatsMembersPort;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PresenceEventListener {

    private final StringRedisTemplate redisTemplate;
    private final WebSocketEventBroadcaster eventBroadcaster;
    private final LoadUserChatsMembersPort loadUserChatsMembersPort;
    private final LoadChatMembersPort loadChatMembersPort;

    @Async
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        try {
            StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
            if (headerAccessor.getUser() instanceof UserPrincipal principal) {
                String userIdStr = principal.getName();
                log.info("User connected: {}", userIdStr);
                UUID userUuid = UUID.fromString(userIdStr);
                redisTemplate.opsForValue().set("presence:" + userIdStr, "ONLINE", Duration.ofMinutes(5));
                
                PresenceStatusResponse presence = PresenceStatusResponse.builder()
                        .userId(userUuid)
                        .isOnline(true)
                        .build();

                // Find all chat members User A shares chats with
                List<ChatMember> userChats = loadUserChatsMembersPort.findByUserId(new UserId(userUuid));
                for (ChatMember userChat : userChats) {
                    List<ChatMember> chatMembers = loadChatMembersPort.findByChatId(userChat.getChatId());
                    for (ChatMember member : chatMembers) {
                        UUID memberId = member.getUserId().value();
                        if (!memberId.equals(userUuid)) {
                            // 1. Notify the partner that user A is now ONLINE
                            eventBroadcaster.broadcastToUser(memberId, "PRESENCE_UPDATE", presence);

                            // 2. Fetch the partner's status and send it to User A
                            String statusStr = redisTemplate.opsForValue().get("presence:" + memberId.toString());
                            boolean isPartnerOnline = "ONLINE".equals(statusStr);
                            Long lastSeenAt = null;
                            if (!isPartnerOnline && statusStr != null) {
                                try {
                                    lastSeenAt = Long.parseLong(statusStr);
                                } catch (NumberFormatException ignored) {}
                            }
                            PresenceStatusResponse partnerPresence = PresenceStatusResponse.builder()
                                    .userId(memberId)
                                    .isOnline(isPartnerOnline)
                                    .lastSeenAt(lastSeenAt)
                                    .build();
                            eventBroadcaster.broadcastToUser(userUuid, "PRESENCE_UPDATE", partnerPresence);
                        }
                    }
                }

                eventBroadcaster.broadcastToUser(userUuid, "PRESENCE_UPDATE", presence);
            }
        } catch (Exception e) {
            log.error("Error processing WebSocket connection event", e);
        }
    }

    @Async
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        try {
            StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
            if (headerAccessor.getUser() instanceof UserPrincipal principal) {
                String userIdStr = principal.getName();
                log.info("User disconnected: {}", userIdStr);
                UUID userUuid = UUID.fromString(userIdStr);
                long now = Instant.now().toEpochMilli();
                redisTemplate.opsForValue().set("presence:" + userIdStr, String.valueOf(now), Duration.ofDays(7));

                PresenceStatusResponse presence = PresenceStatusResponse.builder()
                        .userId(userUuid)
                        .isOnline(false)
                        .lastSeenAt(now)
                        .build();

                // Find all chat members User A shares chats with and notify them User A is offline
                List<ChatMember> userChats = loadUserChatsMembersPort.findByUserId(new UserId(userUuid));
                for (ChatMember userChat : userChats) {
                    List<ChatMember> chatMembers = loadChatMembersPort.findByChatId(userChat.getChatId());
                    for (ChatMember member : chatMembers) {
                        UUID memberId = member.getUserId().value();
                        if (!memberId.equals(userUuid)) {
                            eventBroadcaster.broadcastToUser(memberId, "PRESENCE_UPDATE", presence);
                        }
                    }
                }

                eventBroadcaster.broadcastToUser(userUuid, "PRESENCE_UPDATE", presence);
            }
        } catch (Exception e) {
            log.error("Error processing WebSocket disconnect event", e);
        }
    }
}