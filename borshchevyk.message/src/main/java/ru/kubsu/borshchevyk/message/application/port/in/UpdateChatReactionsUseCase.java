package ru.kubsu.borshchevyk.message.application.port.in;

public interface UpdateChatReactionsUseCase {
    void updateChatReactions(ru.kubsu.borshchevyk.message.application.dto.command.UpdateChatReactionsCommand command);
}