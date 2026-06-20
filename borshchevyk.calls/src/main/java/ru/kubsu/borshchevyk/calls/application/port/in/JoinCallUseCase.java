package ru.kubsu.borshchevyk.calls.application.port.in;

import jakarta.validation.Valid;
import ru.kubsu.borshchevyk.calls.application.dto.command.JoinCallCommand;

public interface JoinCallUseCase {
    /**
     * Generates a token for a user to join an active call.
     *
     * @param command the join command
     * @return LiveKit JWT access token
     */
    String joinCall(@Valid JoinCallCommand command);
}
