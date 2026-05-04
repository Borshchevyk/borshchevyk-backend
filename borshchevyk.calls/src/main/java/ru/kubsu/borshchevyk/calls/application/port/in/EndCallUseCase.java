package ru.kubsu.borshchevyk.calls.application.port.in;

import jakarta.validation.Valid;
import ru.kubsu.borshchevyk.calls.application.dto.command.EndCallCommand;
import ru.kubsu.borshchevyk.calls.domain.model.Call;

public interface EndCallUseCase {
    /**
     * Terminates an active call.
     *
     * @param command the end call command
     * @return the updated Call domain object
     */
    Call endCall(@Valid EndCallCommand command);
}
