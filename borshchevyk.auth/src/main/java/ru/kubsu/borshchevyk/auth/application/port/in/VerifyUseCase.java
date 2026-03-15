package ru.kubsu.borshchevyk.auth.application.port.in;

import ru.kubsu.borshchevyk.auth.application.dto.command.VerifyCommand;
import ru.kubsu.borshchevyk.auth.domain.model.result.VerifyResult;

public interface VerifyUseCase {
    VerifyResult verify(VerifyCommand command);
}
