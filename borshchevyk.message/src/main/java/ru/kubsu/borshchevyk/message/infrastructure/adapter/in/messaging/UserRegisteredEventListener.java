package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.dto.command.CreateChatCommand;
import ru.kubsu.borshchevyk.message.application.port.in.CreateChatUseCase;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatType;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisteredEventListener {

    private final ObjectMapper objectMapper;
    private final CreateChatUseCase createChatUseCase;

    @KafkaListener(topics = "${app.kafka.topics.user-registered}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleUserRegistered(String payload) {
        log.info("Received user-registered event: {}", payload);
        try {
            JsonNode root = objectMapper.readTree(payload);
            if (root.has("id")) {
                UUID userId = UUID.fromString(root.get("id").asText());

                CreateChatCommand command = CreateChatCommand.builder()
                        .creatorId(userId)
                        .type(ChatType.SAVED_MESSAGES)
                        .title("Saved Messages")
                        .description("Your personal saved messages")
                        .initialMemberIds(List.of())
                        .build();

                createChatUseCase.createChat(command);
                log.info("Saved Messages chat created for user: {}", userId);
            } else {
                log.warn("Payload missing 'id' field");
            }
        } catch (Exception e) {
            log.error("Failed to process user-registered event", e);
        }
    }
}
