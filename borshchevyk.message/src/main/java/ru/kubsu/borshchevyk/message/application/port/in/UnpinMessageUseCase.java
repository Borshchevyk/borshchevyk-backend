package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.UnpinMessageCommand;

/**
 * UseCase for unpinning a message in a chat.
 *
 * @author Aleksey Timko
 */
public interface UnpinMessageUseCase {
    void unpinMessage(UnpinMessageCommand command);
}
