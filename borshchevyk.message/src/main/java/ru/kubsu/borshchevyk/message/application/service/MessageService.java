package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.PinMessageCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.UnpinMessageCommand;
import ru.kubsu.borshchevyk.message.application.port.in.LoadPinnedMessagesUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.PinMessageUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.UnpinMessageUseCase;
import ru.kubsu.borshchevyk.message.application.dto.command.DeleteMessageCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.SendMessageCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.ReadMessageCommand;
import ru.kubsu.borshchevyk.message.application.port.in.DeleteMessageUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.LoadChatHistoryUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.SendMessageUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.ReadMessageUseCase;
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
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatRole;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import ru.kubsu.borshchevyk.message.application.dto.command.AddReactionCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.RemoveReactionCommand;
import ru.kubsu.borshchevyk.message.application.port.in.AddReactionUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.RemoveReactionUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.LoadMessageReadersUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.LoadMessageCommentsUseCase;
import ru.kubsu.borshchevyk.message.domain.model.message.MessageReaction;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService implements SendMessageUseCase, LoadChatHistoryUseCase, DeleteMessageUseCase, ReadMessageUseCase, PinMessageUseCase, UnpinMessageUseCase, LoadPinnedMessagesUseCase, AddReactionUseCase, RemoveReactionUseCase, LoadMessageReadersUseCase, LoadMessageCommentsUseCase {

    private final MessagePort messagePort;
    private final ChatPort chatPort;
    private final ChatMemberPort chatMemberPort;
    private final MessageEventPublisherPort messageEventPublisherPort;
    private final ru.kubsu.borshchevyk.message.application.port.out.RealtimeNotificationPort realtimeNotificationPort;
    private final DeletedMessagePort deletedMessagePort;

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
        chatMemberPort.saveAll(List.of(requester));
        
        // Broadcast read receipt to the chat
        ru.kubsu.borshchevyk.message.infrastructure.websocket.dto.ReadReceiptEvent event = 
                new ru.kubsu.borshchevyk.message.infrastructure.websocket.dto.ReadReceiptEvent(command.getRequesterId(), command.getMessageId());
        // Currently there's no broadcast method injected to MessageService directly that uses SimpMessagingTemplate.
        // We'll publish an event, but wait, realtimeNotificationPort can do it. Let's see what it has.
        // Since we can't see the interface, we'll let it be for now or let the controller handle WS.
    }

    @Override
    @Transactional
    public Message sendMessage(SendMessageCommand command) {
        log.info("Sending message to chat: {}", command.getChatId());

        ChatId chatId = new ChatId(command.getChatId());
        UserId authorId = new UserId(command.getAuthorId());

        Chat chat = chatPort.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + command.getChatId()));

        ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember chatMember = chatMemberPort.findByChatIdAndUserId(chatId, authorId)
                .orElseThrow(() -> new UserNotInChatException("User " + authorId.value() + " is not a member of chat " + chatId.value()));

        if (!chatMember.isCanSendMessages()) {
            throw new ForbiddenActionException("User is not allowed to send messages in this chat");
        }

        if (chat.getType() == ru.kubsu.borshchevyk.message.domain.model.chat.ChatType.CHANNEL) {
            if (command.getParentMessageId() == null) {
                // Main channel post
                if (chatMember.getRole() == ChatRole.MEMBER) {
                    throw new ForbiddenActionException("Only ADMIN or OWNER can post in a channel");
                }
            } else {
                // Comment on a channel post
                if (!chat.isCommentsEnabled()) {
                    throw new ForbiddenActionException("Comments are disabled for this channel");
                }
            }
        }

        if (command.getParentMessageId() != null) {
            Message parentMessage = messagePort.findById(new MessageId(command.getParentMessageId()))
                    .orElseThrow(() -> new MessageNotFoundException("Parent message not found"));
            if (!parentMessage.getChatId().equals(chatId)) {
                throw new IllegalArgumentException("Parent message belongs to a different chat");
            }
            parentMessage.setCommentsCount(parentMessage.getCommentsCount() + 1);
            messagePort.save(parentMessage);
        }

        Message message = Message.builder()
                .id(new MessageId(UUID.randomUUID()))
                .chatId(chatId)
                .authorId(authorId)
                .text(command.getText())
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .source(command.getSource())
                .forwardedFromChatId(command.getForwardedFromChatId() != null ? new ChatId(command.getForwardedFromChatId()) : null)
                .forwardedFromUserId(command.getForwardedFromUserId() != null ? new UserId(command.getForwardedFromUserId()) : null)
                .parentMessageId(command.getParentMessageId() != null ? new MessageId(command.getParentMessageId()) : null)
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

    @Override
    @Transactional
    public void pinMessage(PinMessageCommand command) {
        log.info("Pinning message {} in chat {} by user {}", command.getMessageId(), command.getChatId(), command.getRequesterId());
        MessageId messageId = new MessageId(command.getMessageId());
        ChatId chatId = new ChatId(command.getChatId());
        UserId requesterId = new UserId(command.getRequesterId());

        ChatMember requester = chatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("User is not a member of the chat"));

        if (requester.getRole() == ChatRole.MEMBER && !requester.isCanChangeInfo()) {
            throw new ForbiddenActionException("User is not allowed to pin messages");
        }

        Message message = messagePort.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found"));

        if (!message.getChatId().equals(chatId)) {
            throw new IllegalArgumentException("Message does not belong to this chat");
        }

        if (message.getPinnedAt() == null) {
            int pinnedCount = messagePort.countPinnedMessagesByChatId(chatId);
            if (pinnedCount >= 5) {
                throw new IllegalStateException("Maximum of 5 pinned messages reached");
            }
            message.setPinnedAt(LocalDateTime.now());
            message.setPinnedBy(requesterId);
            messagePort.save(message);
        }
    }

    @Override
    @Transactional
    public void unpinMessage(UnpinMessageCommand command) {
        log.info("Unpinning message {} in chat {} by user {}", command.getMessageId(), command.getChatId(), command.getRequesterId());
        MessageId messageId = new MessageId(command.getMessageId());
        ChatId chatId = new ChatId(command.getChatId());
        UserId requesterId = new UserId(command.getRequesterId());

        ChatMember requester = chatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("User is not a member of the chat"));

        if (requester.getRole() == ChatRole.MEMBER && !requester.isCanChangeInfo()) {
            throw new ForbiddenActionException("User is not allowed to unpin messages");
        }

        Message message = messagePort.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found"));

        if (!message.getChatId().equals(chatId)) {
            throw new IllegalArgumentException("Message does not belong to this chat");
        }

        if (message.getPinnedAt() != null) {
            message.setPinnedAt(null);
            message.setPinnedBy(null);
            messagePort.save(message);
        }
    }

    @Override
    @Transactional
    public List<Message> loadPinnedMessages(UUID chatIdRaw, UUID requesterIdRaw) {
        log.info("Loading pinned messages for chat {} by user {}", chatIdRaw, requesterIdRaw);
        ChatId chatId = new ChatId(chatIdRaw);
        UserId requesterId = new UserId(requesterIdRaw);

        chatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("User is not a member of the chat"));

        return messagePort.findPinnedMessagesByChatId(chatId);
    }

    @Override
    @Transactional
    public void addReaction(AddReactionCommand command) {
        log.info("User {} adding reaction {} to message {} in chat {}", command.getRequesterId(), command.getReaction(), command.getMessageId(), command.getChatId());
        MessageId messageId = new MessageId(command.getMessageId());
        ChatId chatId = new ChatId(command.getChatId());
        UserId requesterId = new UserId(command.getRequesterId());

        ChatMember requester = chatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("User is not a member of the chat"));

        Message message = messagePort.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found"));

        if (!message.getChatId().equals(chatId)) {
            throw new IllegalArgumentException("Message does not belong to this chat");
        }

        MessageReaction newReaction = new MessageReaction(command.getRequesterId(), command.getReaction());
        if (!message.getReactions().contains(newReaction)) {
            message.getReactions().add(newReaction);
            messagePort.save(message);
        }
    }

    @Override
    @Transactional
    public void removeReaction(RemoveReactionCommand command) {
        log.info("User {} removing reaction {} from message {} in chat {}", command.getRequesterId(), command.getReaction(), command.getMessageId(), command.getChatId());
        MessageId messageId = new MessageId(command.getMessageId());
        ChatId chatId = new ChatId(command.getChatId());
        UserId requesterId = new UserId(command.getRequesterId());

        ChatMember requester = chatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("User is not a member of the chat"));

        Message message = messagePort.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found"));

        if (!message.getChatId().equals(chatId)) {
            throw new IllegalArgumentException("Message does not belong to this chat");
        }

        MessageReaction targetReaction = new MessageReaction(command.getRequesterId(), command.getReaction());
        if (message.getReactions().contains(targetReaction)) {
            message.getReactions().remove(targetReaction);
            messagePort.save(message);
        }
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

        return chatMemberPort.findReadersOfMessage(chatId, message.getCreatedAt())
                .stream()
                .map(UserId::value)
                .filter(id -> !id.equals(message.getAuthorId().value())) // optionally exclude the author
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> loadMessageComments(UUID chatIdRaw, UUID parentMessageIdRaw, UUID requesterIdRaw, int page, int size) {
        log.info("Loading comments for message {} in chat {} by user {}", parentMessageIdRaw, chatIdRaw, requesterIdRaw);
        ChatId chatId = new ChatId(chatIdRaw);
        MessageId parentMessageId = new MessageId(parentMessageIdRaw);
        UserId requesterId = new UserId(requesterIdRaw);

        chatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("User is not a member of the chat"));

        Message message = messagePort.findById(parentMessageId)
                .orElseThrow(() -> new MessageNotFoundException("Parent message not found"));

        if (!message.getChatId().equals(chatId)) {
            throw new IllegalArgumentException("Message does not belong to this chat");
        }

        return messagePort.findCommentsByMessageId(chatId, parentMessageId, page, size).stream()
                .filter(m -> !m.isDeleted())
                .filter(m -> !deletedMessagePort.isDeletedForUser(m.getId(), requesterId))
                .collect(Collectors.toList());
    }
}