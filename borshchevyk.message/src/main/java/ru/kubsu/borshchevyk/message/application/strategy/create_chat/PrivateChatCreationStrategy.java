package ru.kubsu.borshchevyk.message.application.strategy.create_chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.CreateChatCommand;
import ru.kubsu.borshchevyk.message.application.port.out.*;
import ru.kubsu.borshchevyk.message.domain.exception.ForbiddenActionException;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatRole;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatType;
import ru.kubsu.borshchevyk.message.domain.model.chat.type.PrivateChat;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PrivateChatCreationStrategy implements ChatCreationStrategy {

    private final SaveChatMembersPort saveChatMembersPort;
    private final SaveChatPort saveChatPort;

    private final LoadPrivateChatPort loadPrivateChatPort;

    private final CheckUserPrivacyPort checkUserPrivacyPort;

    @Override
    public ChatType supportedType() {
        return ChatType.PRIVATE;
    }

    @Override
    @Transactional
    public Chat create(CreateChatCommand command) {
        UUID requesterId = command.creatorId();

        if (command.initialMemberIds() == null
                || command.initialMemberIds().size() != 1) {
            throw new IllegalArgumentException(
                    "Private chat must contain exactly one target user"
            );
        }

        UUID targetUserId = command.initialMemberIds().getFirst();

        if (requesterId.equals(targetUserId)) {
            throw new IllegalArgumentException(
                    "Cannot create private chat with yourself"
            );
        }

        if (checkUserPrivacyPort.canInviteToChat(
                targetUserId,
                requesterId
        )) {
            throw new ForbiddenActionException(
                    "User's privacy settings do not allow you to invite them"
            );
        }

        UserId u1 = new UserId(requesterId);
        UserId u2 = new UserId(targetUserId);

        String lockKey =
                requesterId.compareTo(targetUserId) < 0
                        ? (requesterId + "-" + targetUserId).intern()
                        : (targetUserId + "-" + requesterId).intern();

        synchronized (lockKey) {

            Optional<Chat> existing =
                    loadPrivateChatPort.findPrivateChatBetweenUsers(u1, u2);

            if (existing.isPresent()) {
                return existing.get();
            }

            LocalDateTime now = LocalDateTime.now();

            Chat chat = PrivateChat.builder()
                    .id(new ChatId(UUID.randomUUID()))
                    .type(ChatType.PRIVATE)
                    .createdAt(now)
                    .build();

            chat = saveChatPort.save(chat);

            ChatMember member1 = ChatMember.builder()
                    .chatId(chat.getId())
                    .userId(u1)
                    .role(ChatRole.MEMBER)
                    .joinedAt(now)
                    .lastReadAt(now)
                    .build();

            ChatMember member2 = ChatMember.builder()
                    .chatId(chat.getId())
                    .userId(u2)
                    .role(ChatRole.MEMBER)
                    .joinedAt(now)
                    .lastReadAt(now)
                    .build();

            saveChatMembersPort.saveAll(List.of(member1, member2));

            return chat;
        }
    }
}