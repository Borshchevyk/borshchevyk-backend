package ru.kubsu.borshchevyk.auth.application.port.in;

import ru.kubsu.borshchevyk.auth.application.dto.command.LoginCommand;
import ru.kubsu.borshchevyk.auth.domain.model.result.LoginResult;

/**
 * Use case for logging into an account.
 */
public interface LoginUseCase {
    /**
     * Authenticates a user and returns a login result.
     *
     * @param command the login command containing credentials
     * @return the login result with authentication data
     */
    LoginResult login(LoginCommand command);
}
