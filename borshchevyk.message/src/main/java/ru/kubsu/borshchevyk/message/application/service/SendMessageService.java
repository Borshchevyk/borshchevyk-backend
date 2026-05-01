package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.kubsu.borshchevyk.message.application.dto.command.SendMessageCommand;
import ru.kubsu.borshchevyk.message.application.port.in.SendMessageUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.ChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.ChatPort;
import ru.kubsu.borshchevyk.message.application.port.out.MediaPort;
import ru.kubsu.borshchevyk.message.application.port.out.MessageEventPublisherPort;
import ru.kubsu.borshchevyk.message.application.port.out.MessagePort;
import ru.kubsu.borshchevyk.message.domain.exception.ChatNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.ForbiddenActionException;
import ru.kubsu.borshchevyk.message.domain.exception.MessageNotFoundException;
import ru.kubsu.borshchevyk.message.domain.exception.UserNotInChatException;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatRole;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * SendMessageService implementation.
 *
 * @author Aleksey Timko
 * @since 2026-05-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SendMessageService implements SendMessageUseCase {

    private final MessagePort messagePort;
    private final ChatPort chatPort;
    private final ChatMemberPort chatMemberPort;
    private final MessageEventPublisherPort messageEventPublisherPort;
    private final MediaPort mediaPort;
    private final ru.kubsu.borshchevyk.message.application.port.out.RealtimeNotificationPort realtimeNotificationPort;

    @Override
    @Transactional
    public Message sendMessage(SendMessageCommand command) {
        log.info("Sending message to chat: {}", command.chatId());

        ChatId chatId = new ChatId(command.chatId());
        UserId authorId = new UserId(command.authorId());

        Chat chat = chatPort.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + command.chatId()));

        ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember chatMember = chatMemberPort.findByChatIdAndUserId(chatId, authorId)
                .orElseThrow(() -> new UserNotInChatException("User " + authorId.value() + " is not a member of chat " + chatId.value()));

        if (!chat.canMemberSendMessage(chatMember, command.parentMessageId() != null)) {
            throw new ForbiddenActionException("User is not allowed to send messages in this chat");
        }

        List<ru.kubsu.borshchevyk.message.domain.model.message.MessageAttachment> attachments = new java.util.ArrayList<>();
        if (command.attachmentIds() != null && !command.attachmentIds().isEmpty()) {
            List<ru.kubsu.borshchevyk.message.application.dto.response.AttachmentMetadataDto> validAttachments = mediaPort.validateAttachments(command.attachmentIds(), command.authorId());
            if (validAttachments == null || validAttachments.isEmpty() || validAttachments.size() != command.attachmentIds().size()) {
                throw new IllegalArgumentException("Invalid attachments. Make sure they are uploaded and ready.");
            }
            attachments = validAttachments.stream()
                    .map(m -> new ru.kubsu.borshchevyk.message.domain.model.message.MessageAttachment(
                            m.id(),
                            m.type(),
                            m.originalFilename(),
                            m.extension(),
                            m.sizeBytes(),
                            m.duration(),
                            m.thumbnailId()
                    ))                    .collect(Collectors.toList());
        }

        if (command.parentMessageId() != null) {
            Message parentMessage = messagePort.findById(new MessageId(command.parentMessageId()))
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
                .text(command.text())
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .source(command.source())
                .forwardedFromChatId(command.forwardedFromChatId() != null ? new ChatId(command.forwardedFromChatId()) : null)
                .forwardedFromUserId(command.forwardedFromUserId() != null ? new UserId(command.forwardedFromUserId()) : null)
                .parentMessageId(command.parentMessageId() != null ? new MessageId(command.parentMessageId()) : null)
                .attachments(attachments)
                .build();

        Message savedMessage = messagePort.save(message);

        // Update the author's read status to the message they just sent
        chatMember.setLastReadMessageId(savedMessage.getId());
        chatMember.setLastReadAt(savedMessage.getCreatedAt());
        chatMemberPort.saveAll(List.of(chatMember));

        List<ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember> members = chatMemberPort.findByChatId(chatId);
        List<String> memberIds = members.stream().map(m -> m.getUserId().value().toString()).collect(Collectors.toList());

        messageEventPublisherPort.publishMessageCreatedEvent(savedMessage, memberIds);

        // Notify via realtimeNotificationPort to update unread counters on clients
        for (String memberId : memberIds) {
            if (!memberId.equals(authorId.value().toString())) {
                realtimeNotificationPort.notifyChatEvent(
                        new ru.kubsu.borshchevyk.message.domain.model.value.UserId(UUID.fromString(memberId)),
                        chatId,
                        "MESSAGE"
                );
            }
        }

        log.info("Message sent successfully with ID: {}", savedMessage.getId().value());
        return savedMessage;
    }
}