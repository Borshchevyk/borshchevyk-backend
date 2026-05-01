package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.InviteUserCommand;

/**
 * UseCase for inviting a user to a chat.
 *
 * @author Aleksey Timko
 */
public interface InviteUserUseCase {
    void inviteUser(InviteUserCommand command);
}
