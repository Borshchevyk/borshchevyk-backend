package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kubsu.borshchevyk.message.application.dto.response.PresenceStatusResponse;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/presence")
@RequiredArgsConstructor
@Tag(name = "Presence", description = "Endpoints for user online status")
public class PresenceController {

    private final StringRedisTemplate redisTemplate;

    @Operation(summary = "Get user presence", description = "Retrieves the online status of a user.")
    @GetMapping("/{userId}")
    public PresenceStatusResponse getUserPresence(@PathVariable UUID userId) {
        String statusStr = redisTemplate.opsForValue().get("presence:" + userId.toString());
        if (statusStr != null) {
            if ("ONLINE".equals(statusStr)) {
                return new PresenceStatusResponse(userId, true, null);
            } else {
                try {
                    long lastSeen = Long.parseLong(statusStr);
                    return new PresenceStatusResponse(userId, false, lastSeen);
                } catch (NumberFormatException e) {
                    return new PresenceStatusResponse(userId, false, null);
                }
            }
        }
        return new PresenceStatusResponse(userId, false, null);
    }
}
