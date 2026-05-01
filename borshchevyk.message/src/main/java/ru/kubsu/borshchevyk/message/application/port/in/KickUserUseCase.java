package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.KickUserCommand;

/**
 * UseCase for kicking a user from a chat.
 *
 * @author Aleksey Timko
 */
public interface KickUserUseCase {
    void kickUser(KickUserCommand command);
}
