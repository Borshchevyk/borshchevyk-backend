package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.ReadMessageCommand;
import ru.kubsu.borshchevyk.message.application.port.in.ReadMessageUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.*;
import ru.kubsu.borshchevyk.message.domain.exception.ChatNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.MessageNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReadMessageService implements ReadMessageUseCase {

    private final SaveMessageReaderPort saveMessageReaderPort;
    private final SaveMessagePort saveMessagePort;
    private final LoadMessagePort loadMessagePort;
    private final SaveChatMembersPort saveChatMembersPort;
    private final LoadChatPort loadChatPort;
    private final LoadChatMemberPort loadChatMemberPort;

    @Override
    @Transactional
    public void readMessage(ReadMessageCommand command) {
        log.info("Reading message {} in chat {} by user {}", command.messageId(), command.chatId(), command.requesterId());
        
        ChatId chatId = new ChatId(command.chatId());
        UserId requesterId = new UserId(command.requesterId());
        MessageId messageId = new MessageId(command.messageId());

        loadChatPort.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));

        ChatMember requester = loadChatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("Requester is not in the chat"));

        Message message = loadMessagePort.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found"));
                
        if (!message.getChatId().equals(chatId)) {
            throw new IllegalArgumentException("Message does not belong to this chat");
        }

        if (requester.getLastReadAt() == null || message.getCreatedAt().isAfter(requester.getLastReadAt())) {
            requester.setLastReadMessageId(messageId);
            requester.setLastReadAt(message.getCreatedAt());
            saveChatMembersPort.saveAll(List.of(requester));
        }

        if (!message.getAuthorId().equals(requesterId) && message.getStatus() != ru.kubsu.borshchevyk.message.domain.model.message.MessageStatus.READ) {
            message.setStatus(ru.kubsu.borshchevyk.message.domain.model.message.MessageStatus.READ);
            saveMessagePort.save(message);
        }

        saveMessageReaderPort.save(messageId, requesterId);
    }
}