package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.LeaveChatCommand;

/**
 * UseCase for leaving a chat.
 *
 * @author Aleksey Timko
 */
public interface LeaveChatUseCase {
    void leaveChat(LeaveChatCommand command);
}
