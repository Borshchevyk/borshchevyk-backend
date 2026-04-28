package ru.kubsu.borshchevyk.user.application.port.in;

import ru.kubsu.borshchevyk.user.application.dto.command.RemoveContactCommand;

/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
public interface RemoveContactUseCase {
    void removeContact(RemoveContactCommand command);
}

