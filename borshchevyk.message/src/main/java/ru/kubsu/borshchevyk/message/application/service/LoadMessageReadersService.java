package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.message.application.dto.query.LoadMessageReadersQuery;
import ru.kubsu.borshchevyk.message.application.port.in.LoadMessageReadersUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.LoadMessagePort;
import ru.kubsu.borshchevyk.message.application.port.out.LoadMessageReadersPort;
import ru.kubsu.borshchevyk.message.domain.exception.MessageNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoadMessageReadersService implements LoadMessageReadersUseCase {

    private final LoadMessagePort loadMessagePort;
    private final LoadChatMemberPort loadChatMemberPort;
    private final LoadMessageReadersPort loadMessageReadersPort;

    @Override
    public List<UUID> loadMessageReaders(LoadMessageReadersQuery query) {
        log.info("Loading readers for message {} in chat {} by user {}", query.messageId(), query.chatId(), query.requesterId());
        ChatId chatId = new ChatId(query.chatId());
        MessageId messageId = new MessageId(query.messageId());
        UserId requesterId = new UserId(query.requesterId());

        loadChatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("User is not a member of the chat"));

        Message message = loadMessagePort.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found"));

        if (!message.getChatId().equals(chatId)) {
            throw new IllegalArgumentException("Message does not belong to this chat");
        }

        return loadMessageReadersPort.findReaders(messageId)
                .stream()
                .map(UserId::value)
                .filter(id -> !id.equals(message.getAuthorId().value()))
                .collect(Collectors.toList());
    }
}