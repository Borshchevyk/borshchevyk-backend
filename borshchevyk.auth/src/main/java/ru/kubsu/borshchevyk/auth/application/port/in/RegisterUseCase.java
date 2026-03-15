package ru.kubsu.borshchevyk.auth.application.port.in;

import ru.kubsu.borshchevyk.auth.application.dto.command.RegisterCommand;
import ru.kubsu.borshchevyk.auth.domain.model.result.RegisterResult;

public interface RegisterUseCase {
    RegisterResult register(RegisterCommand command);
}
