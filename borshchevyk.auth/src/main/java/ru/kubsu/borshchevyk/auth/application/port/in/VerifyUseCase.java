package ru.kubsu.borshchevyk.auth.application.port.in;

import ru.kubsu.borshchevyk.auth.application.dto.command.VerifyCommand;
import ru.kubsu.borshchevyk.auth.domain.model.result.VerifyResult;

/**
 * Use case for verifying an account using a challenge.
 */
public interface VerifyUseCase {
    /**
     * Verifies the account based on the provided signature.
     *
     * @param command the verification command
     * @return the verification result
     */
    VerifyResult verify(VerifyCommand command);
}
