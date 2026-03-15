package ru.kubsu.borshchevyk.auth.application.port.in;

import ru.kubsu.borshchevyk.auth.application.dto.command.ChallengeCommand;
import ru.kubsu.borshchevyk.auth.domain.model.result.ChallengeResult;

public interface ChallengeUseCase {
    ChallengeResult challenge(ChallengeCommand command);
}
