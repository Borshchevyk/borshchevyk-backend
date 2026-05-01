package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.kubsu.borshchevyk.message.application.port.in.LoadChatHistoryUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.LoadMessageCommentsUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.LoadPinnedMessagesUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.ChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.ChatPort;
import ru.kubsu.borshchevyk.message.application.port.out.DeletedMessagePort;
import ru.kubsu.borshchevyk.message.application.port.out.MessagePort;
import ru.kubsu.borshchevyk.message.domain.exception.ChatNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.MessageNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * MessageHistoryService implementation.
 *
 * @author Aleksey Timko
 * @since 2026-05-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageHistoryService implements LoadChatHistoryUseCase, LoadMessageCommentsUseCase, LoadPinnedMessagesUseCase {

    private final MessagePort messagePort;
    private final ChatPort chatPort;
    private final ChatMemberPort chatMemberPort;
    private final DeletedMessagePort deletedMessagePort;

    @Override
    @Transactional(readOnly = true)
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

        return messagePort.loadChatHistory(chatId, userId, chatMember.getHistoryClearedAt(), page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> loadPinnedMessages(UUID chatIdRaw, UUID requesterIdRaw) {
        log.info("Loading pinned messages for chat {} by user {}", chatIdRaw, requesterIdRaw);
        ChatId chatId = new ChatId(chatIdRaw);
        UserId requesterId = new UserId(requesterIdRaw);

        chatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("User is not a member of the chat"));

        return messagePort.findPinnedMessagesByChatId(chatId);
    }

    @Override
    @Transactional(readOnly = true)
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

        return messagePort.loadMessageComments(chatId, parentMessageId, requesterId, page, size);
    }
}
