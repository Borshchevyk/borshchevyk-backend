package ru.kubsu.borshchevyk.user.application.port.in;

import ru.kubsu.borshchevyk.user.application.dto.command.RemoveContactCommand;

public interface RemoveContactUseCase {
    void removeContact(RemoveContactCommand command);
}
