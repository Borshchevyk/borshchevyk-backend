package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.DeleteChatCommand;

/**
 * UseCase for deleting a chat.
 *
 * @author Aleksey Timko
 */
public interface DeleteChatUseCase {
    void deleteChat(DeleteChatCommand command);
}
