package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.PinMessageCommand;
import ru.kubsu.borshchevyk.message.application.port.in.PinMessageUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.*;
import ru.kubsu.borshchevyk.message.domain.exception.ChatNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.ForbiddenActionException;
import ru.kubsu.borshchevyk.message.domain.exception.MessageNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PinMessageService implements PinMessageUseCase {

    private final SaveMessagePort saveMessagePort;
    private final LoadMessagePort loadMessagePort;
    private final LoadChatPort loadChatPort;
    private final CountPinnedMessagesPort countPinnedMessagesPort;
    private final LoadChatMemberPort loadChatMemberPort;

    @Override
    @Transactional
    public void pinMessage(PinMessageCommand command) {
        log.info("Pinning message {} in chat {} by user {}", command.messageId(), command.chatId(), command.requesterId());
        MessageId messageId = new MessageId(command.messageId());
        ChatId chatId = new ChatId(command.chatId());
        UserId requesterId = new UserId(command.requesterId());

        Chat chat = loadChatPort.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));

        ChatMember requester = loadChatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("User is not a member of the chat"));

        if (chat.canMemberPinMessage(requester)) {
            throw new ForbiddenActionException("User is not allowed to pin messages");
        }

        Message message = loadMessagePort.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found"));

        if (!message.getChatId().equals(chatId)) {
            throw new IllegalArgumentException("Message does not belong to this chat");
        }

        if (message.getPinnedAt() == null) {
            int pinnedCount = countPinnedMessagesPort.countPinnedMessagesByChatId(chatId);
            if (pinnedCount >= 5) {
                throw new IllegalStateException("Maximum of 5 pinned messages reached");
            }
            message.setPinnedAt(LocalDateTime.now());
            message.setPinnedBy(requesterId);
            saveMessagePort.save(message);
        }
    }
}