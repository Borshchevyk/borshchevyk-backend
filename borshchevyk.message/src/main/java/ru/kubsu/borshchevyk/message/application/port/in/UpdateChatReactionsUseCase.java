package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.UpdateChatReactionsCommand;

/**
 * UseCase for updating allowed reactions in a chat.
 *
 * @author Aleksey Timko
 */
public interface UpdateChatReactionsUseCase {
    void updateChatReactions(UpdateChatReactionsCommand command);
}
