package ru.kubsu.borshchevyk.auth.application.port.in;

import ru.kubsu.borshchevyk.auth.application.dto.command.RefreshCommand;
import ru.kubsu.borshchevyk.auth.domain.model.result.VerifyResult;

public interface RefreshUseCase {
    VerifyResult refresh(RefreshCommand command);
}
