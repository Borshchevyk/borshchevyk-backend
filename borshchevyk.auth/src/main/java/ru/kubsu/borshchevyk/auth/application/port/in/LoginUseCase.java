package ru.kubsu.borshchevyk.auth.application.port.in;

import ru.kubsu.borshchevyk.auth.application.dto.command.LoginCommand;
import ru.kubsu.borshchevyk.auth.domain.model.result.LoginResult;

public interface LoginUseCase {
    LoginResult login(LoginCommand command);
}
