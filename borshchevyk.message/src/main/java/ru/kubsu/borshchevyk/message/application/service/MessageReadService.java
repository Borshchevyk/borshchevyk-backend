package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.ReadMessageCommand;
import ru.kubsu.borshchevyk.message.application.port.in.LoadMessageReadersUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.ReadMessageUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.ChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.ChatPort;
import ru.kubsu.borshchevyk.message.application.port.out.MessagePort;
import ru.kubsu.borshchevyk.message.application.port.out.MessageReaderPort;
import ru.kubsu.borshchevyk.message.domain.exception.ChatNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.MessageNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
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
public class MessageReadService implements ReadMessageUseCase, LoadMessageReadersUseCase {

    private final MessagePort messagePort;
    private final ChatPort chatPort;
    private final ChatMemberPort chatMemberPort;
    private final MessageReaderPort messageReaderPort;

    @Override
    @Transactional
    public void readMessage(ReadMessageCommand command) {
        log.info("Reading message {} in chat {} by user {}", command.getMessageId(), command.getChatId(), command.getRequesterId());
        
        ChatId chatId = new ChatId(command.getChatId());
        UserId requesterId = new UserId(command.getRequesterId());
        MessageId messageId = new MessageId(command.getMessageId());

        Chat chat = chatPort.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));

        ChatMember requester = chatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("Requester is not in the chat"));

        Message message = messagePort.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found"));
                
        if (!message.getChatId().equals(chatId)) {
            throw new IllegalArgumentException("Message does not belong to this chat");
        }

        requester.setLastReadMessageId(messageId);
        requester.setLastReadAt(message.getCreatedAt());
        chatMemberPort.saveAll(List.of(requester));

        // Update message status to READ if someone other than author reads it
        if (!message.getAuthorId().equals(requesterId) && message.getStatus() != ru.kubsu.borshchevyk.message.domain.model.message.MessageStatus.READ) {
            message.setStatus(ru.kubsu.borshchevyk.message.domain.model.message.MessageStatus.READ);
            messagePort.save(message);
        }

        // Exact tracking
        messageReaderPort.save(messageId, requesterId);
    }

    @Override
    public List<UUID> loadMessageReaders(UUID chatIdRaw, UUID messageIdRaw, UUID requesterIdRaw) {
        log.info("Loading readers for message {} in chat {} by user {}", messageIdRaw, chatIdRaw, requesterIdRaw);
        ChatId chatId = new ChatId(chatIdRaw);
        MessageId messageId = new MessageId(messageIdRaw);
        UserId requesterId = new UserId(requesterIdRaw);

        chatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("User is not a member of the chat"));

        Message message = messagePort.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found"));

        if (!message.getChatId().equals(chatId)) {
            throw new IllegalArgumentException("Message does not belong to this chat");
        }

        return messageReaderPort.findReaders(messageId)
                .stream()
                .map(UserId::value)
                .filter(id -> !id.equals(message.getAuthorId().value()))
                .collect(Collectors.toList());
    }
}
