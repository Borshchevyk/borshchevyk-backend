package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.DeleteMessageCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.SendMessageCommand;
import ru.kubsu.borshchevyk.message.application.port.in.DeleteMessageUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.LoadChatHistoryUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.SendMessageUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.ChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.ChatPort;
import ru.kubsu.borshchevyk.message.application.port.out.DeletedMessagePort;
import ru.kubsu.borshchevyk.message.application.port.out.MessageEventPublisherPort;
import ru.kubsu.borshchevyk.message.application.port.out.MessagePort;
import ru.kubsu.borshchevyk.message.domain.exception.ChatNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.MessageNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.ForbiddenActionException;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService implements SendMessageUseCase, LoadChatHistoryUseCase, DeleteMessageUseCase {

    private final MessagePort messagePort;
    private final ChatPort chatPort;
    private final ChatMemberPort chatMemberPort;
    private final MessageEventPublisherPort messageEventPublisherPort;
    private final ru.kubsu.borshchevyk.message.application.port.out.RealtimeNotificationPort realtimeNotificationPort;
    private final DeletedMessagePort deletedMessagePort;

    @Override
    @Transactional
    public Message sendMessage(SendMessageCommand command) {
        log.info("Sending message to chat: {}", command.getChatId());

        ChatId chatId = new ChatId(command.getChatId());
        UserId authorId = new UserId(command.getAuthorId());

        chatPort.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + command.getChatId()));

        ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember chatMember = chatMemberPort.findByChatIdAndUserId(chatId, authorId)
                .orElseThrow(() -> new UserNotInChatException("User " + authorId.value() + " is not a member of chat " + chatId.value()));

        if (!chatMember.isCanSendMessages()) {
            throw new ForbiddenActionException("User is not allowed to send messages in this chat");
        }

        Message message = Message.builder()
                .id(new MessageId(UUID.randomUUID()))
                .chatId(chatId)
                .authorId(authorId)
                .text(command.getText())
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .source(command.getSource())
                .build();

        Message savedMessage = messagePort.save(message);

        List<ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember> members = chatMemberPort.findByChatId(chatId);
        List<String> memberIds = members.stream().map(m -> m.getUserId().value().toString()).collect(Collectors.toList());

        messageEventPublisherPort.publishMessageCreatedEvent(savedMessage, memberIds);
        for (ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember member : members) {
            realtimeNotificationPort.notifyUser(member.getUserId(), savedMessage);
        }

        log.info("Message sent successfully with ID: {}", savedMessage.getId().value());
        return savedMessage;
    }

    @Override
    public List<Message> loadChatHistory(UUID chatIdRaw, UUID userIdRaw, int page, int size) {
        log.info("Loading chat history for chat: {}", chatIdRaw);

        ChatId chatId = new ChatId(chatIdRaw);
        UserId userId = new UserId(userIdRaw);

        Chat chat = chatPort.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + chatIdRaw));

        if (chat.isDeleted()) {
            throw new ChatNotFoundException("Chat not found or deleted with id: " + chatIdRaw);
        }

        ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember chatMember = chatMemberPort.findByChatIdAndUserId(chatId, userId)
                .orElseThrow(() -> new UserNotInChatException("User " + userIdRaw + " is not a member of chat " + chatIdRaw));

        List<Message> messages = messagePort.findByChatId(chatId, page, size);

        return messages.stream()
                .filter(m -> !m.isDeleted())
                .filter(m -> !deletedMessagePort.isDeletedForUser(m.getId(), userId))
                .filter(m -> chatMember.getHistoryClearedAt() == null || !m.getCreatedAt().isBefore(chatMember.getHistoryClearedAt()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteMessage(DeleteMessageCommand command) {
        log.info("Deleting message: {}", command.getMessageId());
        MessageId messageId = new MessageId(command.getMessageId());
        UserId requesterId = new UserId(command.getRequesterId());

        Message message = messagePort.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found with id: " + command.getMessageId()));

        ChatId chatId = message.getChatId();

        ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember requester = chatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("User is not a member of the chat"));

        if (command.isForAll()) {
            boolean isAuthor = message.getAuthorId().equals(requesterId);
            boolean isAdminOrOwner = requester.getRole() == ru.kubsu.borshchevyk.message.domain.model.chat.ChatRole.ADMIN || requester.getRole() == ru.kubsu.borshchevyk.message.domain.model.chat.ChatRole.OWNER;
            boolean canDelete = requester.isCanDeleteMessages();

            if (isAuthor || (isAdminOrOwner && canDelete)) {
                message.setDeleted(true);
                messagePort.save(message);

                List<ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember> members = chatMemberPort.findByChatId(chatId);
                List<String> memberIds = members.stream().map(m -> m.getUserId().value().toString()).collect(Collectors.toList());

                messageEventPublisherPort.publishMessageDeletedEvent(message, memberIds);
            } else {
                throw new ForbiddenActionException("User is not allowed to delete this message for everyone");
            }
        } else {
            deletedMessagePort.save(messageId, requesterId);
        }
    }
}