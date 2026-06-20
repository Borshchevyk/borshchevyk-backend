package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.RemoveReactionCommand;
import ru.kubsu.borshchevyk.message.application.port.in.RemoveReactionUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.LoadChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.LoadMessagePort;
import ru.kubsu.borshchevyk.message.application.port.out.SaveMessagePort;
import ru.kubsu.borshchevyk.message.domain.exception.MessageNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.message.MessageReaction;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

@Slf4j
@Service
@RequiredArgsConstructor
public class RemoveReactionService implements RemoveReactionUseCase {

    private final SaveMessagePort saveMessagePort;
    private final LoadMessagePort loadMessagePort;
    private final LoadChatMemberPort loadChatMemberPort;

    @Override
    @Transactional
    public void removeReaction(RemoveReactionCommand command) {
        log.info("User {} removing reaction {} from message {} in chat {}", command.requesterId(), command.reaction(), command.messageId(), command.chatId());
        MessageId messageId = new MessageId(command.messageId());
        ChatId chatId = new ChatId(command.chatId());
        UserId requesterId = new UserId(command.requesterId());

        loadChatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("User is not a member of the chat"));

        Message message = loadMessagePort.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found"));

        if (!message.getChatId().equals(chatId)) {
            throw new IllegalArgumentException("Message does not belong to this chat");
        }

        MessageReaction targetReaction = new MessageReaction(command.requesterId(), command.reaction());
        if (message.getReactions().contains(targetReaction)) {
            message.getReactions().remove(targetReaction);
            saveMessagePort.save(message);
        }
    }
}