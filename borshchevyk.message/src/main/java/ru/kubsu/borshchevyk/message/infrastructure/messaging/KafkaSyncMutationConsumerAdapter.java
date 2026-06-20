package ru.kubsu.borshchevyk.message.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.dto.command.DeleteMessageCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.SendMessageCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.UpdateMessageCommand;
import ru.kubsu.borshchevyk.message.application.port.in.DeleteMessageUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.SendMessageUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.UpdateMessageUseCase;
import ru.kubsu.borshchevyk.message.domain.exception.MessageNotFoundException;

import java.util.UUID;

/**
 * Adapter for consuming offline mutations pushed from the sync-service.
 *
 * @author Aleksey Timko
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaSyncMutationConsumerAdapter {

    private final SendMessageUseCase sendMessageUseCase;
    private final UpdateMessageUseCase updateMessageUseCase;
    private final DeleteMessageUseCase deleteMessageUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.topics.sync-mutations:sync-mutations}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeSyncMutation(String payload) {
        log.info("Received sync mutation from Kafka");
        try {
            JsonNode root = objectMapper.readTree(payload);
            String eventType = root.get("eventType").asText();
            
            // The payload field in SyncEvent is stringified JSON containing the CRDT operation
            String operationPayload = root.get("payload").asText();
            JsonNode opNode = objectMapper.readTree(operationPayload);

            switch (eventType) {
                case "MESSAGE_CREATED" -> handleMessageCreated(root, opNode);
                case "MESSAGE_UPDATED" -> handleMessageUpdated(opNode);
                case "MESSAGE_DELETED" -> handleMessageDeleted(opNode);
                default -> log.debug("Ignored sync mutation of type {}", eventType);
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to parse sync mutation payload", e);
        } catch (Exception e) {
            log.error("Error processing sync mutation", e);
        }
    }

    private void handleMessageCreated(JsonNode root, JsonNode opNode) {
        try {
            UUID chatId = UUID.fromString(root.get("entityId").asText());
            UUID authorId = UUID.fromString(root.get("userId").asText());

            String text = opNode.has("text") ? opNode.get("text").asText() : null;
            
            UUID forwardedFromChatId = (opNode.has("forwardedFromChatId") && !opNode.get("forwardedFromChatId").isNull()) 
                    ? UUID.fromString(opNode.get("forwardedFromChatId").asText()) : null;
            
            UUID forwardedFromUserId = (opNode.has("forwardedFromUserId") && !opNode.get("forwardedFromUserId").isNull()) 
                    ? UUID.fromString(opNode.get("forwardedFromUserId").asText()) : null;
            
            UUID parentMessageId = (opNode.has("parentMessageId") && !opNode.get("parentMessageId").isNull()) 
                    ? UUID.fromString(opNode.get("parentMessageId").asText()) : null;
            
            java.util.List<UUID> attachmentIds = new java.util.ArrayList<>();
            if (opNode.has("attachmentIds") && opNode.get("attachmentIds").isArray()) {
                for (JsonNode idNode : opNode.get("attachmentIds")) {
                    attachmentIds.add(UUID.fromString(idNode.asText()));
                }
            }

            SendMessageCommand command = SendMessageCommand.builder()
                    .chatId(chatId)
                    .authorId(authorId)
                    .text(text)
                    .source(ru.kubsu.borshchevyk.message.domain.model.message.MessageSource.OFFLINE)
                    .forwardedFromChatId(forwardedFromChatId)
                    .forwardedFromUserId(forwardedFromUserId)
                    .parentMessageId(parentMessageId)
                    .attachmentIds(attachmentIds)
                    .build();

            sendMessageUseCase.sendMessage(command);
        } catch (Exception e) {
            log.error("Failed to apply MESSAGE_CREATED sync mutation", e);
        }
    }

    private void handleMessageUpdated(JsonNode opNode) {
        try {
            UUID messageId = UUID.fromString(opNode.get("messageId").asText());
            UUID chatId = UUID.fromString(opNode.get("chatId").asText());
            UUID requesterId = UUID.fromString(opNode.get("requesterId").asText());
            String text = opNode.get("text").asText();

            UpdateMessageCommand command = new UpdateMessageCommand(messageId, chatId, requesterId, text, true);
            updateMessageUseCase.updateMessage(command);
        } catch (MessageNotFoundException e) {
            log.warn("Could not apply MESSAGE_UPDATED sync mutation: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Failed to apply MESSAGE_UPDATED sync mutation", e);
        }
    }

    private void handleMessageDeleted(JsonNode opNode) {
        try {
            UUID messageId = UUID.fromString(opNode.get("messageId").asText());
            UUID requesterId = UUID.fromString(opNode.get("requesterId").asText());
            boolean forAll = opNode.has("forAll") ? opNode.get("forAll").asBoolean() : true;

            DeleteMessageCommand command = new DeleteMessageCommand(messageId, requesterId, forAll, true);
            deleteMessageUseCase.deleteMessage(command);
        } catch (MessageNotFoundException e) {
            log.warn("Could not apply MESSAGE_DELETED sync mutation: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Failed to apply MESSAGE_DELETED sync mutation", e);
        }
    }
}
