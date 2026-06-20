package ru.kubsu.borshchevyk.message.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.CreateChatCommand;
import ru.kubsu.borshchevyk.message.application.port.in.CreateChatUseCase;
import ru.kubsu.borshchevyk.message.application.strategy.create_chat.ChatCreationStrategy;
import ru.kubsu.borshchevyk.message.domain.event.chat.ChatCreatedEvent;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatType;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CreateChatService implements CreateChatUseCase {

    private final Map<ChatType, ChatCreationStrategy> strategies;
    private final ApplicationEventPublisher eventPublisher;

    public CreateChatService(List<ChatCreationStrategy> strategies, ApplicationEventPublisher eventPublisher) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(
                        ChatCreationStrategy::supportedType,
                        Function.identity()
                ));
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Chat createChat(CreateChatCommand command) {
        log.info("Creating {} chat", command.type());

        ChatCreationStrategy strategy = strategies.get(command.type());

        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported chat type: " + command.type());
        }

        Chat chat = strategy.create(command);
        
        eventPublisher.publishEvent(new ChatCreatedEvent(
                this,
                chat.getId().value(),
                command.creatorId(),
                command.initialMemberIds()
        ));

        return chat;
    }
}