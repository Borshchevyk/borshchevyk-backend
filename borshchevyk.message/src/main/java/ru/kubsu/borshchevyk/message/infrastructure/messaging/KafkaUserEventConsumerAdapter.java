package ru.kubsu.borshchevyk.message.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatMembersPort;
import ru.kubsu.borshchevyk.message.application.port.out.LoadUserChatsMembersPort;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.infrastructure.websocket.WebSocketEventBroadcaster;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Adapter for consuming user events from Kafka and broadcasting them to chat partners via WebSocket.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaUserEventConsumerAdapter {

    private final WebSocketEventBroadcaster eventBroadcaster;
    private final LoadUserChatsMembersPort loadUserChatsMembersPort;
    private final LoadChatMembersPort loadChatMembersPort;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.topics.user-updated}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserUpdated(String payload) {
        log.info("Received user updated event from Kafka: {}", payload);
        try {
            JsonNode root = objectMapper.readTree(payload);
            String userIdStr = root.get("userId").asText();
            UUID userIdVal = UUID.fromString(userIdStr);

            // Find all unique chat partners of the updated user
            Set<UUID> targetUserIds = new HashSet<>();
            targetUserIds.add(userIdVal); // Notify the user themselves to sync across sessions

            List<ChatMember> userChats = loadUserChatsMembersPort.findByUserId(new UserId(userIdVal));
            for (ChatMember chatMember : userChats) {
                List<ChatMember> members = loadChatMembersPort.findByChatId(chatMember.getChatId());
                for (ChatMember member : members) {
                    targetUserIds.add(member.getUserId().value());
                }
            }

            // Broadcast the USER_UPDATED event payload to each partner via WebSocket
            for (UUID targetUserId : targetUserIds) {
                eventBroadcaster.broadcastToUser(targetUserId, "USER_UPDATED", root);
            }

            log.info("Broadcasted USER_UPDATED event to {} users", targetUserIds.size());
        } catch (JsonProcessingException e) {
            log.error("Failed to parse user updated payload", e);
        } catch (Exception e) {
            log.error("Error processing user updated event", e);
        }
    }
}
