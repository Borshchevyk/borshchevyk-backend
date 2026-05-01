package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.PinChatCommand;

/**
 * UseCase for pinning a chat for a user.
 *
 * @author Aleksey Timko
 */
public interface PinChatUseCase {
    void pinChat(PinChatCommand command);
}
