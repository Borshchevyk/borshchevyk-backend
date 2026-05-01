package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.ClearChatHistoryCommand;

/**
 * UseCase for clearing chat history.
 *
 * @author Aleksey Timko
 */
public interface ClearChatHistoryUseCase {
    void clearChatHistory(ClearChatHistoryCommand command);
}
