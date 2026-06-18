package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.UpdateMessageCommand;
import ru.kubsu.borshchevyk.message.application.port.in.UpdateMessageUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.*;
import ru.kubsu.borshchevyk.message.domain.exception.ForbiddenActionException;
import ru.kubsu.borshchevyk.message.domain.exception.MessageNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateMessageService implements UpdateMessageUseCase {

    private final SaveMessagePort saveMessagePort;
    private final LoadMessagePort loadMessagePort;
    private final LoadChatMemberPort loadChatMemberPort;
    private final NotifyUserPort notifyUserPort;

    private final LoadChatMembersPort loadChatMembersPort;

    @Override
    @Transactional
    public Message updateMessage(UpdateMessageCommand command) {
        log.info("Updating message: {}", command.messageId());

        MessageId messageId = new MessageId(command.messageId());
        ChatId chatId = new ChatId(command.chatId());
        UserId requesterId = new UserId(command.requesterId());

        Message message = loadMessagePort.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found with id: " + command.messageId()));

        if (!message.getChatId().equals(chatId)) {
            throw new IllegalArgumentException("Message does not belong to this chat");
        }

        loadChatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("User is not a member of the chat"));

        if (!message.getAuthorId().equals(requesterId)) {
            throw new ForbiddenActionException("Only the author can update this message");
        }

        message.setText(command.text());
        message.setUpdatedAt(LocalDateTime.now());
        Message updatedMessage = saveMessagePort.save(message);

        List<ChatMember> members = loadChatMembersPort.findByChatId(chatId);
        List<String> memberIds = members.stream().map(m -> m.getUserId().value().toString()).collect(Collectors.toList());

        for (String targetId : memberIds) {
            notifyUserPort.notifyUser(new UserId(java.util.UUID.fromString(targetId)), updatedMessage);
        }

        log.info("Message updated successfully with ID: {}", updatedMessage.getId().value());
        return updatedMessage;
    }
}