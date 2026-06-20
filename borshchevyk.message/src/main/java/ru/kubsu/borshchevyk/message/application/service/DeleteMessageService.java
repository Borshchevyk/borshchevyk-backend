package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.kubsu.borshchevyk.message.infrastructure.websocket.WebSocketEventBroadcaster;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.DeleteMessageCommand;
import ru.kubsu.borshchevyk.message.application.port.in.DeleteMessageUseCase;
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

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteMessageService implements DeleteMessageUseCase {

    private final SaveMessagePort saveMessagePort;
    private final SaveDeletedMessagePort saveDeletedMessagePort;

    private final LoadMessagePort loadMessagePort;

    private final LoadChatPort loadChatPort;

    private final LoadChatMemberPort loadChatMemberPort;
    private final LoadChatMembersPort loadChatMembersPort;

    private final MessageEventPublisherPort messageEventPublisherPort;
    private final WebSocketEventBroadcaster eventBroadcaster;

    @Override
    @Transactional
    public void deleteMessage(DeleteMessageCommand command) {
        log.info("Deleting message: {}", command.messageId());
        MessageId messageId = new MessageId(command.messageId());
        UserId requesterId = new UserId(command.requesterId());

        Message message = loadMessagePort.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found with id: " + command.messageId()));

        ChatId chatId = message.getChatId();

        Chat chat = loadChatPort.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));

        ChatMember requester = loadChatMemberPort.findByChatIdAndUserId(chatId, requesterId)
                .orElseThrow(() -> new UserNotInChatException("User is not a member of the chat"));

        if (command.forAll()) {
            boolean isAuthor = message.getAuthorId().equals(requesterId);

            if (chat.canMemberDeleteMessage(requester, isAuthor)) {
                message.setDeleted(true);
                saveMessagePort.save(message);

                if (!command.isSyncMutation()) {
                    List<ChatMember> members = loadChatMembersPort.findByChatId(chatId);
                    List<String> memberIds = members.stream().map(m -> m.getUserId().value().toString()).collect(Collectors.toList());

                    messageEventPublisherPort.publishMessageDeletedEvent(message, memberIds);
                }
            } else {
                throw new ForbiddenActionException("User is not allowed to delete this message for everyone");
            }
        } else {
            saveDeletedMessagePort.save(messageId, requesterId);
            eventBroadcaster.broadcastToUser(requesterId.value(), "MESSAGE_DELETED", messageId.value());
        }
    }
}