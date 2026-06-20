package ru.kubsu.borshchevyk.message.application.strategy.create_chat;

import ru.kubsu.borshchevyk.message.application.dto.command.CreateChatCommand;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatType;

public interface ChatCreationStrategy {
    ChatType supportedType();
    Chat create(CreateChatCommand command);
}