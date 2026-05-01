package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.SendMessageCommand;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;

/**
 * UseCase for sending a new message.
 *
 * @author Aleksey Timko
 */
public interface SendMessageUseCase {
    Message sendMessage(SendMessageCommand command);
}
