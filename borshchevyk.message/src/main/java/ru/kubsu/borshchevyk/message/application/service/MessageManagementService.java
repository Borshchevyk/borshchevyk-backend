package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.kubsu.borshchevyk.message.application.dto.command.DeleteMessageCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.PinMessageCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.UnpinMessageCommand;
import ru.kubsu.borshchevyk.message.application.port.in.DeleteMessageUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.PinMessageUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.UnpinMessageUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.ChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.DeletedMessagePort;
import ru.kubsu.borshchevyk.message.application.port.out.MessageEventPublisherPort;
import ru.kubsu.borshchevyk.message.application.port.out.MessagePort;
import ru.kubsu.borshchevyk.message.domain.exception.ForbiddenActionException;
import ru.kubsu.borshchevyk.message.domain.exception.MessageNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatRole;
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
public class MessageManagementService implements DeleteMessageUseCase, PinMessageUseCase, UnpinMessageUseCase {

    private final MessagePort messagePort;
    private final ChatMemberPort chatMemberPort;
    private final MessageEventPublisherPort messageEventPublisherPort;
    private final DeletedMessagePort deletedMessagePort;

    @Override
    @Transactional
    public void deleteMessage(DeleteMessageCommand command) {
        log.info("Deleting message: {}", command.getMessageId());
        MessageId messageId = new MessageId(command.getMessageId());
        UserId requesterId = new UserId(command.getRequesterId());

        Message message = messagePort.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found with id: " + command.getMessageId()));

        ChatId chatId = message.getChatId();

        ChatMember requester = chatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("User is not a member of the chat"));

        if (command.isForAll()) {
            boolean isAuthor = message.getAuthorId().equals(requesterId);
            boolean isAdminOrOwner = requester.getRole() == ChatRole.ADMIN || requester.getRole() == ChatRole.OWNER;
            boolean canDelete = requester.isCanDeleteMessages();

            if (isAuthor || (isAdminOrOwner && canDelete)) {
                message.setDeleted(true);
                messagePort.save(message);

                List<ChatMember> members = chatMemberPort.findByChatId(chatId);
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
}
