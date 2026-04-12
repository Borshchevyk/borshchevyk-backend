package ru.kubsu.borshchevyk.message.infrastructure.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class PresenceEventListener {

    private final StringRedisTemplate redisTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        if (headerAccessor.getUser() instanceof UserPrincipal principal) {
            String userId = principal.getName();
            log.info("User connected: {}", userId);
            redisTemplate.opsForValue().set("presence:" + userId, "ONLINE", Duration.ofMinutes(5));
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        if (headerAccessor.getUser() instanceof UserPrincipal principal) {
            String userId = principal.getName();
            log.info("User disconnected: {}", userId);
            // Save last seen timestamp instead of deleting
            redisTemplate.opsForValue().set("presence:" + userId, String.valueOf(Instant.now().toEpochMilli()), Duration.ofDays(7));
        }
    }
}
