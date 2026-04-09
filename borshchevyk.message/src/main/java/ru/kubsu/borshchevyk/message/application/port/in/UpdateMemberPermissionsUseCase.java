package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.UpdatePermissionsCommand;

public interface UpdateMemberPermissionsUseCase {
    void updatePermissions(UpdatePermissionsCommand command);
}