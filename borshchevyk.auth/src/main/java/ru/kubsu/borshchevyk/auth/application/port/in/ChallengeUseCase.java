package ru.kubsu.borshchevyk.auth.application.port.in;

import ru.kubsu.borshchevyk.auth.application.dto.command.ChallengeCommand;
import ru.kubsu.borshchevyk.auth.domain.model.result.ChallengeResult;

/**
 * Use case for generating a verification challenge.
 */
public interface ChallengeUseCase {
    /**
     * Generates a challenge for the account specified in the command.
     *
     * @param command the challenge generation command
     * @return the challenge result containing the challenge string
     */
    ChallengeResult challenge(ChallengeCommand command);
}
