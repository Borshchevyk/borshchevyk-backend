package ru.kubsu.borshchevyk.auth.application.port.in;

import ru.kubsu.borshchevyk.auth.application.dto.command.RefreshCommand;
import ru.kubsu.borshchevyk.auth.domain.model.result.VerifyResult;

/**
 * Use case for refreshing an authentication token.
 */
public interface RefreshUseCase {

    /**
     * Refreshes the authentication tokens.
     *
     * @param command the refresh command containing the refresh token
     * @return the result containing new tokens
     */
    VerifyResult refresh(RefreshCommand command);
}
