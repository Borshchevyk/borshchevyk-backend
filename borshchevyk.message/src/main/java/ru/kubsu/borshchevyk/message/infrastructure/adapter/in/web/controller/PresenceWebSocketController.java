package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import ru.kubsu.borshchevyk.message.application.dto.response.PresenceStatusResponse;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PresenceWebSocketController {

    private final StringRedisTemplate redisTemplate;

    @SubscribeMapping("/user/{userId}/presence")
    public PresenceStatusResponse getInitialPresence(@DestinationVariable UUID userId) {
        String statusStr = redisTemplate.opsForValue().get("presence:" + userId.toString());
        boolean isOnline = "ONLINE".equals(statusStr);
        Long lastSeenAt = null;
        
        if (!isOnline && statusStr != null) {
            try {
                lastSeenAt = Long.parseLong(statusStr);
            } catch (NumberFormatException ignored) {}
        }
        
        return new PresenceStatusResponse(userId, isOnline, lastSeenAt);
    }
}
