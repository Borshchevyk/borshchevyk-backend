package ru.kubsu.borshchevyk.calls.application.port.in;

import jakarta.validation.Valid;
import ru.kubsu.borshchevyk.calls.application.dto.command.InitiateCallCommand;
import ru.kubsu.borshchevyk.calls.domain.model.Call;

public interface InitiateCallUseCase {
    /**
     * Initiates a new call with the given participants.
     *
     * @param command the initiation command
     * @return the created Call domain object
     */
    Call initiateCall(@Valid InitiateCallCommand command);
}
