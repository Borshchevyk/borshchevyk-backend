package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.UpdateChatReactionsCommand;

public interface UpdateChatReactionsUseCase {
    void updateChatReactions(UpdateChatReactionsCommand command);
}