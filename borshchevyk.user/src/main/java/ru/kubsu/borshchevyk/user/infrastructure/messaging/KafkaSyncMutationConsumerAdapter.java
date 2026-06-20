package ru.kubsu.borshchevyk.user.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.user.application.dto.command.EditUserCommand;
import ru.kubsu.borshchevyk.user.application.dto.command.UpdateProfileCommand;
import ru.kubsu.borshchevyk.user.application.port.in.DeleteUserUseCase;
import ru.kubsu.borshchevyk.user.application.port.in.EditUserUseCase;
import ru.kubsu.borshchevyk.user.application.port.in.UpdateProfileUseCase;
import ru.kubsu.borshchevyk.user.domain.exception.UserNotFoundException;

/**
 * Adapter for consuming offline mutations pushed from the sync-service.
 *
 * @author Aleksey Timko
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaSyncMutationConsumerAdapter {

    private final EditUserUseCase editUserUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.topics.sync-mutations:sync-mutations}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeSyncMutation(String payload) {
        log.info("Received sync mutation from Kafka");
        try {
            JsonNode root = objectMapper.readTree(payload);
            String eventType = root.get("eventType").asText();

            String operationPayload = root.get("payload").asText();
            JsonNode opNode = objectMapper.readTree(operationPayload);

            switch (eventType) {
                case "USER_UPDATED" -> handleUserUpdated(opNode);
                case "USER_DELETED" -> handleUserDeleted(opNode);
                default -> log.debug("Ignored sync mutation of type {}", eventType);
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to parse sync mutation payload", e);
        } catch (Exception e) {
            log.error("Error processing sync mutation", e);
        }
    }

    private void handleUserUpdated(JsonNode opNode) {
        try {
            String userId = opNode.get("userId").asText();
            
            // Depending on what the client actually sent, we might call EditUser or UpdateProfile.
            // Let's assume it's a full profile update for CRDT.
            String firstName = opNode.has("firstName") ? opNode.get("firstName").asText() : null;
            String lastName = opNode.has("lastName") ? opNode.get("lastName").asText() : null;
            String bio = opNode.has("bio") ? opNode.get("bio").asText() : null;
            String avatarUrl = opNode.has("avatarUrl") ? opNode.get("avatarUrl").asText() : null;

            UpdateProfileCommand profileCommand = new UpdateProfileCommand(userId, firstName, lastName, bio, avatarUrl, true);
            updateProfileUseCase.updateProfile(profileCommand);

            // Also check for email/tag changes (EditUserUseCase)
            String email = opNode.has("email") ? opNode.get("email").asText() : null;
            String tag = opNode.has("tag") ? opNode.get("tag").asText() : null;
            if (email != null || tag != null) {
                EditUserCommand editCommand = new EditUserCommand(userId, email, tag, true);
                editUserUseCase.editUser(editCommand);
            }

        } catch (UserNotFoundException e) {
            log.warn("Could not apply USER_UPDATED sync mutation: User not found");
        } catch (Exception e) {
            log.error("Failed to apply USER_UPDATED sync mutation", e);
        }
    }

    private void handleUserDeleted(JsonNode opNode) {
        try {
            String userId = opNode.get("userId").asText();
            deleteUserUseCase.deleteUser(userId, true);
        } catch (Exception e) {
            log.error("Failed to apply USER_DELETED sync mutation", e);
        }
    }
}
