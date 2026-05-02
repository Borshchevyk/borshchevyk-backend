package ru.kubsu.borshchevyk.message.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ru.kubsu.borshchevyk.message.application.port.out.ChatEventPublisherPort;
import ru.kubsu.borshchevyk.message.domain.exception.MessagingSerializationException;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Aleksey Timko
 * @since 2026-05-01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaChatEventPublisherAdapter implements ChatEventPublisherPort {

    private static final String TOPIC = "chats.events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publishChatEvent(UserId userId, ChatId chatId, String action) {
        Map<String, String> event = new HashMap<>();
        event.put("userId", userId.value().toString());
        event.put("chatId", chatId.value().toString());
        event.put("action", action);

        Runnable publishAction = () -> {
            try {
                String payload = objectMapper.writeValueAsString(event);
                kafkaTemplate.send(TOPIC, chatId.value().toString(), payload);
                log.info("Published ChatEvent to topic {}: {}", TOPIC, payload);
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize ChatEvent", e);
                throw new MessagingSerializationException("Failed to serialize event", e);
            }
        };

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publishAction.run();
                }
            });
        } else {
            publishAction.run();
        }
    }
}
