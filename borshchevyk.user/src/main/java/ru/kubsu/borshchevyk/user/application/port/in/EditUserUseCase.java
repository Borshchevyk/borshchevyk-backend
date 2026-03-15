package ru.kubsu.borshchevyk.user.application.port.in;

import ru.kubsu.borshchevyk.user.application.dto.command.EditUserCommand;
import ru.kubsu.borshchevyk.user.domain.model.result.EditUserResult;

public interface EditUserUseCase {
    EditUserResult editUser(EditUserCommand command);
}
