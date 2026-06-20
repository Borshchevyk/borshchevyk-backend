package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.CreateChatCommand;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;

public interface CreateChatUseCase {
    Chat createChat(CreateChatCommand command);
}