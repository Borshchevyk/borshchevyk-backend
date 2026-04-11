package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.InviteUserCommand;

public interface InviteUserUseCase {
    void inviteUser(InviteUserCommand command);
}
