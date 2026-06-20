package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.KickUserCommand;

public interface KickUserUseCase {
    void kickUser(KickUserCommand command);
}