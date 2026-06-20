package ru.kubsu.borshchevyk.message.infrastructure.websocket.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import ru.kubsu.borshchevyk.message.infrastructure.websocket.WebSocketEventBroadcaster;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import ru.kubsu.borshchevyk.message.application.dto.response.PresenceStatusResponse;
import ru.kubsu.borshchevyk.message.domain.model.user.UserPrincipal;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PresenceEventListener {

    private final StringRedisTemplate redisTemplate;
    private final WebSocketEventBroadcaster eventBroadcaster;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        if (headerAccessor.getUser() instanceof UserPrincipal principal) {
            String userIdStr = principal.getName();
            log.info("User connected: {}", userIdStr);
            redisTemplate.opsForValue().set("presence:" + userIdStr, "ONLINE", Duration.ofMinutes(5));
            
            PresenceStatusResponse presence = PresenceStatusResponse.builder()
                    .userId(UUID.fromString(userIdStr))
                    .isOnline(true)
                    .build();
            eventBroadcaster.broadcastToAll("PRESENCE_UPDATE", presence);
            eventBroadcaster.broadcastToUser(UUID.fromString(userIdStr), "PRESENCE_UPDATE", presence);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        if (headerAccessor.getUser() instanceof UserPrincipal principal) {
            String userIdStr = principal.getName();
            log.info("User disconnected: {}", userIdStr);
            long now = Instant.now().toEpochMilli();
            redisTemplate.opsForValue().set("presence:" + userIdStr, String.valueOf(now), Duration.ofDays(7));

            PresenceStatusResponse presence = PresenceStatusResponse.builder()
                    .userId(UUID.fromString(userIdStr))
                    .isOnline(false)
                    .lastSeenAt(now)
                    .build();
            eventBroadcaster.broadcastToAll("PRESENCE_UPDATE", presence);
            eventBroadcaster.broadcastToUser(UUID.fromString(userIdStr), "PRESENCE_UPDATE", presence);
        }
    }
}