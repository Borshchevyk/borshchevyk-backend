package ru.kubsu.borshchevyk.auth.application.port.in;

import ru.kubsu.borshchevyk.auth.application.dto.command.RegisterCommand;
import ru.kubsu.borshchevyk.auth.domain.model.result.RegisterResult;

/**
 * Use case for registering a new account.
 */
public interface RegisterUseCase {
    /**
     * Registers a new account and returns the registration result.
     *
     * @param command the registration command
     * @return the registration result
     */
    RegisterResult register(RegisterCommand command);
}
