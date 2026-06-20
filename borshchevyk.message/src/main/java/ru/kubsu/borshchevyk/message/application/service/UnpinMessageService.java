package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.UnpinMessageCommand;
import ru.kubsu.borshchevyk.message.application.port.in.UnpinMessageUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatPort;
import ru.kubsu.borshchevyk.message.application.port.out.LoadMessagePort;
import ru.kubsu.borshchevyk.message.application.port.out.SaveMessagePort;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class UnpinMessageService implements UnpinMessageUseCase {

    private final SaveMessagePort saveMessagePort;
    private final LoadMessagePort loadMessagePort;
    private final LoadChatMemberPort loadChatMemberPort;
    private final LoadChatPort loadChatPort;

    @Override
    @Transactional
    public void unpinMessage(UnpinMessageCommand command) {
        log.info("Unpinning message {} in chat {} by user {}", command.messageId(), command.chatId(), command.requesterId());
        MessageId messageId = new MessageId(command.messageId());
        ChatId chatId = new ChatId(command.chatId());
        UserId requesterId = new UserId(command.requesterId());

        Chat chat = loadChatPort.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));

        ChatMember requester = loadChatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("User is not a member of the chat"));

        if (chat.canMemberPinMessage(requester)) {
            throw new ForbiddenActionException("User is not allowed to unpin messages");
        }

        Message message = loadMessagePort.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found"));

        if (!message.getChatId().equals(chatId)) {
            throw new IllegalArgumentException("Message does not belong to this chat");
        }

        if (message.getPinnedAt() != null) {
            message.setPinnedAt(null);
            message.setPinnedBy(null);
            saveMessagePort.save(message);
        }
    }
}