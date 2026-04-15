package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.AddReactionCommand;
import ru.kubsu.borshchevyk.message.application.dto.command.RemoveReactionCommand;
import ru.kubsu.borshchevyk.message.application.port.in.AddReactionUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.RemoveReactionUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.ChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.ChatPort;
import ru.kubsu.borshchevyk.message.application.port.out.MessagePort;
import ru.kubsu.borshchevyk.message.domain.exception.ChatNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.MessageNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.message.MessageReaction;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageReactionService implements AddReactionUseCase, RemoveReactionUseCase {

    private final MessagePort messagePort;
    private final ChatPort chatPort;
    private final ChatMemberPort chatMemberPort;

    @Override
    @Transactional
    public void addReaction(AddReactionCommand command) {
        log.info("User {} adding reaction {} to message {} in chat {}", command.getRequesterId(), command.getReaction(), command.getMessageId(), command.getChatId());
        MessageId messageId = new MessageId(command.getMessageId());
        ChatId chatId = new ChatId(command.getChatId());
        UserId requesterId = new UserId(command.getRequesterId());

        Chat chat = chatPort.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));

        ChatMember requester = chatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("User is not a member of the chat"));

        Message message = messagePort.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found"));

        if (!message.getChatId().equals(chatId)) {
            throw new IllegalArgumentException("Message does not belong to this chat");
        }

        if (chat.getAllowedReactions() != null && !chat.getAllowedReactions().contains(command.getReaction())) {
            throw new IllegalArgumentException("Reaction not allowed in this chat");
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
}
