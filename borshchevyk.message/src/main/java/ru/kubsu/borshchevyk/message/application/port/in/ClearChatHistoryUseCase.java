package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.ClearChatHistoryCommand;

public interface ClearChatHistoryUseCase {
    void clearChatHistory(ClearChatHistoryCommand command);
}
