package ru.kubsu.borshchevyk.message.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.CreateChatCommand;
import ru.kubsu.borshchevyk.message.application.port.in.CreateChatUseCase;
import ru.kubsu.borshchevyk.message.application.port.in.CreatePrivateChatUseCase;
import ru.kubsu.borshchevyk.message.application.port.out.ChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.ChatPort;
import ru.kubsu.borshchevyk.message.application.port.out.CheckUserPrivacyPort;
import ru.kubsu.borshchevyk.message.domain.exception.ForbiddenActionException;
import ru.kubsu.borshchevyk.message.domain.model.chat.Channel;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatRole;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatType;
import ru.kubsu.borshchevyk.message.domain.model.chat.GroupChat;
import ru.kubsu.borshchevyk.message.domain.model.chat.PrivateChat;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * CreateChatService implementation.
 *
 * @author Aleksey Timko
 * @since 2026-05-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreateChatService implements CreateChatUseCase, CreatePrivateChatUseCase {

    private final ChatPort chatPort;
    private final ChatMemberPort chatMemberPort;
    private final CheckUserPrivacyPort checkUserPrivacyPort;

    @Override
    @Transactional
    public Chat createPrivateChat(UUID requesterId, UUID targetUserId) {
        log.info("Creating private chat between {} and {}", requesterId, targetUserId);
        
        if (requesterId.equals(targetUserId)) {
            throw new IllegalArgumentException("Cannot create private chat with yourself. Use Saved Messages.");
        }
        
        if (!checkUserPrivacyPort.canInviteToChat(targetUserId, requesterId)) {
            throw new ForbiddenActionException("User's privacy settings do not allow you to invite them");
        }
        
        UserId u1 = new UserId(requesterId);
        UserId u2 = new UserId(targetUserId);

        // Using synchronized block to prevent concurrent creation of duplicates for the same pair in this JVM.
        String lockKey = (requesterId.compareTo(targetUserId) < 0 
                ? requesterId.toString() + "-" + targetUserId.toString() 
                : targetUserId.toString() + "-" + requesterId.toString()).intern();

        synchronized (lockKey) {
            Optional<Chat> existingChat = chatPort.findPrivateChatBetweenUsers(u1, u2);
            if (existingChat.isPresent()) {
                return existingChat.get();
            }

            Chat chat = PrivateChat.builder()
                    .id(new ChatId(UUID.randomUUID()))
                    .type(ChatType.PRIVATE)
                    .createdAt(LocalDateTime.now())
                    .build();
            chat = chatPort.save(chat);

            ChatMember member1 = ChatMember.builder()
                    .chatId(chat.getId())
                    .userId(u1)
                    .role(ChatRole.MEMBER)
                    .joinedAt(LocalDateTime.now())
                    .lastReadAt(LocalDateTime.now())
                    .build();

            ChatMember member2 = ChatMember.builder()
                    .chatId(chat.getId())
                    .userId(u2)
                    .role(ChatRole.MEMBER)
                    .joinedAt(LocalDateTime.now())
                    .lastReadAt(LocalDateTime.now())
                    .build();

            chatMemberPort.saveAll(List.of(member1, member2));

            return chat;
        }
    }

    @Override
    @Transactional
    public Chat createChat(CreateChatCommand command) {
        log.info("Creating chat with title: {}", command.title());

        Chat chat;
        ChatId newChatId = new ChatId(UUID.randomUUID());
        if (command.type() == ChatType.GROUP) {
            chat = GroupChat.builder()
                    .id(newChatId)
                    .type(command.type())
                    .title(command.title())
                    .description(command.description())
                    .createdAt(LocalDateTime.now())
                    .build();
        } else if (command.type() == ChatType.CHANNEL) {
            chat = Channel.builder()
                    .id(newChatId)
                    .type(command.type())
                    .title(command.title())
                    .description(command.description())
                    .createdAt(LocalDateTime.now())
                    .build();
        } else if (command.type() == ChatType.SAVED_MESSAGES) {
            chat = ru.kubsu.borshchevyk.message.domain.model.chat.SavedMessages.builder()
                    .id(newChatId)
                    .type(command.type())
                    .createdAt(LocalDateTime.now())
                    .isDeletable(false)
                    .build();
        } else {
             throw new IllegalArgumentException("Unsupported chat type for general creation: " + command.type());
        }

        chat = chatPort.save(chat);

        List<ChatMember> members = new ArrayList<>();

        ChatMember creator = ChatMember.builder()
                .chatId(chat.getId())
                .userId(new UserId(command.creatorId()))
                .role(ChatRole.OWNER)
                .joinedAt(LocalDateTime.now())
                .lastReadAt(LocalDateTime.now())
                .build();
        members.add(creator);

        if (command.initialMemberIds() != null) {
            for (UUID memberId : command.initialMemberIds()) {
                if (!memberId.equals(command.creatorId())) {
                    if (!checkUserPrivacyPort.canInviteToChat(memberId, command.creatorId())) {
                        throw new ForbiddenActionException("User's privacy settings do not allow you to invite them");
                    }
                    
                    members.add(ChatMember.builder()
                            .chatId(chat.getId())
                            .userId(new UserId(memberId))
                            .role(ChatRole.MEMBER)
                            .joinedAt(LocalDateTime.now())
                            .lastReadAt(LocalDateTime.now())
                            .build());
                }
            }
        }

        chatMemberPort.saveAll(members);

        log.info("Chat created successfully with ID: {}", chat.getId().value());
        return chat;
    }
}