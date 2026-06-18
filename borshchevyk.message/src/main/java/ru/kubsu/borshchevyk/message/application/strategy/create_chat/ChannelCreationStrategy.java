package ru.kubsu.borshchevyk.message.application.strategy.create_chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.CreateChatCommand;
import ru.kubsu.borshchevyk.message.application.port.out.SaveChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.SaveChatPort;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatRole;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatType;
import ru.kubsu.borshchevyk.message.domain.model.chat.type.Channel;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChannelCreationStrategy implements ChatCreationStrategy {

    private final SaveChatPort saveChatPort;
    private final SaveChatMemberPort saveChatMemberPort;

    @Override
    public ChatType supportedType() {
        return ChatType.CHANNEL;
    }

    @Override
    @Transactional
    public Chat create(CreateChatCommand command) {

        LocalDateTime now = LocalDateTime.now();

        Chat chat = Channel.builder()
                .id(new ChatId(UUID.randomUUID()))
                .type(ChatType.CHANNEL)
                .title(command.title())
                .description(command.description())
                .createdAt(now)
                .build();

        chat = saveChatPort.save(chat);

        ChatMember owner = ChatMember.builder()
                .chatId(chat.getId())
                .userId(new UserId(command.creatorId()))
                .role(ChatRole.OWNER)
                .joinedAt(now)
                .lastReadAt(now)
                .build();

        saveChatMemberPort.save(owner);

        return chat;
    }
}