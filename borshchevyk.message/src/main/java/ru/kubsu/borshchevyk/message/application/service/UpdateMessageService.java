package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.kubsu.borshchevyk.message.application.dto.command.UpdateMessageCommand;
import ru.kubsu.borshchevyk.message.application.port.in.UpdateMessageUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.ChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.MessageEventPublisherPort;
import ru.kubsu.borshchevyk.message.application.port.out.MessagePort;
import ru.kubsu.borshchevyk.message.domain.exception.ForbiddenActionException;
import ru.kubsu.borshchevyk.message.domain.exception.MessageNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.application.port.out.RealtimeNotificationPort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateMessageService implements UpdateMessageUseCase {

    private final MessagePort messagePort;
    private final ChatMemberPort chatMemberPort;
    private final MessageEventPublisherPort messageEventPublisherPort;
    private final RealtimeNotificationPort realtimeNotificationPort;

    @Override
    @Transactional
    public Message updateMessage(UpdateMessageCommand command) {
        log.info("Updating message: {}", command.getMessageId());

        MessageId messageId = new MessageId(command.getMessageId());
        ChatId chatId = new ChatId(command.getChatId());
        UserId requesterId = new UserId(command.getRequesterId());

        Message message = messagePort.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found with id: " + command.getMessageId()));

        if (!message.getChatId().equals(chatId)) {
            throw new IllegalArgumentException("Message does not belong to this chat");
        }

        ChatMember requester = chatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("User is not a member of the chat"));

        if (!message.getAuthorId().equals(requesterId)) {
            throw new ForbiddenActionException("Only the author can update this message");
        }

        message.setText(command.getText());
        message.setUpdatedAt(LocalDateTime.now());
        Message updatedMessage = messagePort.save(message);

        List<ChatMember> members = chatMemberPort.findByChatId(chatId);
        List<String> memberIds = members.stream().map(m -> m.getUserId().value().toString()).collect(Collectors.toList());

        // Notify via WebSockets directly here for updates (or through a new publishMessageUpdatedEvent method if needed)
        for (String targetId : memberIds) {
            realtimeNotificationPort.notifyUser(new UserId(java.util.UUID.fromString(targetId)), updatedMessage);
        }

        log.info("Message updated successfully with ID: {}", updatedMessage.getId().value());
        return updatedMessage;
    }
}
