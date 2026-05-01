package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.DeleteMessageCommand;

/**
 * UseCase for deleting a message.
 *
 * @author Aleksey Timko
 */
public interface DeleteMessageUseCase {
    void deleteMessage(DeleteMessageCommand command);
}
