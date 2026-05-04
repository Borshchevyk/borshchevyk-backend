package ru.kubsu.borshchevyk.calls.application.port.in;

import jakarta.validation.Valid;
import ru.kubsu.borshchevyk.calls.application.dto.command.LeaveCallCommand;
import ru.kubsu.borshchevyk.calls.domain.model.Call;

public interface LeaveCallUseCase {
    /**
     * Handles a user leaving the call.
     *
     * @param command the leave command
     * @return the updated Call domain object
     */
    Call leaveCall(@Valid LeaveCallCommand command);
}
