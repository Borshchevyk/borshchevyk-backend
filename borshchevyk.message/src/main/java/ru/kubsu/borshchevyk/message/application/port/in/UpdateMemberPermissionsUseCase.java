package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.UpdatePermissionsCommand;

/**
 * UseCase for updating member permissions in a chat.
 *
 * @author Aleksey Timko
 */
public interface UpdateMemberPermissionsUseCase {
    void updatePermissions(UpdatePermissionsCommand command);
}
