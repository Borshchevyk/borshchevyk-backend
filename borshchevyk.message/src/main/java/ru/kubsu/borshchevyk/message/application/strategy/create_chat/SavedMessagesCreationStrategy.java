package ru.kubsu.borshchevyk.message.application.strategy.create_chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.message.application.dto.command.CreateChatCommand;
import ru.kubsu.borshchevyk.message.application.port.out.SaveChatMemberPort;
import ru.kubsu.borshchevyk.message.application.port.out.SaveChatPort;
import ru.kubsu.borshchevyk.message.domain.model.chat.*;
import ru.kubsu.borshchevyk.message.domain.model.chat.type.SavedMessages;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SavedMessagesCreationStrategy implements ChatCreationStrategy {

    private final SaveChatPort saveChatPort;
    private final SaveChatMemberPort saveChatMemberPort;

    @Override
    public ChatType supportedType() {
        return ChatType.SAVED_MESSAGES;
    }

    @Override
    @Transactional
    public Chat create(CreateChatCommand command) {

        LocalDateTime now = LocalDateTime.now();

        Chat chat = SavedMessages.builder()
                .id(new ChatId(UUID.randomUUID()))
                .type(ChatType.SAVED_MESSAGES)
                .createdAt(now)
                .isDeletable(false)
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