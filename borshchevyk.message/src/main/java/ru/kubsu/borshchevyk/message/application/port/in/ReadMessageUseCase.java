package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.ReadMessageCommand;

/**
 * UseCase for marking a message as read.
 *
 * @author Aleksey Timko
 */
public interface ReadMessageUseCase {
    void readMessage(ReadMessageCommand command);
}
