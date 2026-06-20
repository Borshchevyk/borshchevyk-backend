package ru.kubsu.borshchevyk.message.application.strategy.create_chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.CreateChatCommand;
import ru.kubsu.borshchevyk.message.application.port.out.CheckUserPrivacyPort;
import ru.kubsu.borshchevyk.message.application.port.out.SaveChatMembersPort;
import ru.kubsu.borshchevyk.message.application.port.out.SaveChatPort;
import ru.kubsu.borshchevyk.message.domain.exception.ForbiddenActionException;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatRole;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatType;
import ru.kubsu.borshchevyk.message.domain.model.chat.type.GroupChat;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class GroupChatCreationStrategy implements ChatCreationStrategy {

    private final SaveChatMembersPort saveChatMembersPort;
    private final SaveChatPort saveChatPort;

    private final CheckUserPrivacyPort checkUserPrivacyPort;

    @Override
    public ChatType supportedType() {
        return ChatType.GROUP;
    }

    @Override
    @Transactional
    public Chat create(CreateChatCommand command) {

        LocalDateTime now = LocalDateTime.now();

        Chat chat = GroupChat.builder()
                .id(new ChatId(UUID.randomUUID()))
                .type(ChatType.GROUP)
                .title(command.title())
                .description(command.description())
                .createdAt(now)
                .build();

        chat = saveChatPort.save(chat);

        List<ChatMember> members = new ArrayList<>();

        members.add(
                ChatMember.builder()
                        .chatId(chat.getId())
                        .userId(new UserId(command.creatorId()))
                        .role(ChatRole.OWNER)
                        .joinedAt(now)
                        .lastReadAt(now)
                        .build()
        );

        if (command.initialMemberIds() != null) {
            for (UUID memberId : command.initialMemberIds()) {

                if (memberId.equals(command.creatorId())) {
                    continue;
                }

                if (checkUserPrivacyPort.canInviteToChat(
                        memberId,
                        command.creatorId()
                )) {
                    throw new ForbiddenActionException(
                            "User's privacy settings do not allow you to invite them"
                    );
                }

                members.add(
                        ChatMember.builder()
                                .chatId(chat.getId())
                                .userId(new UserId(memberId))
                                .role(ChatRole.MEMBER)
                                .joinedAt(now)
                                .lastReadAt(now)
                                .build()
                );
            }
        }

        saveChatMembersPort.saveAll(members);

        return chat;
    }
}