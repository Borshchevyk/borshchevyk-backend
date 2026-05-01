package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.UnpinChatCommand;

/**
 * UseCase for unpinning a chat for a user.
 *
 * @author Aleksey Timko
 */
public interface UnpinChatUseCase {
    void unpinChat(UnpinChatCommand command);
}
