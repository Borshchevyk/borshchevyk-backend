package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.PinMessageCommand;

/**
 * UseCase for pinning a message in a chat.
 *
 * @author Aleksey Timko
 */
public interface PinMessageUseCase {
    void pinMessage(PinMessageCommand command);
}
