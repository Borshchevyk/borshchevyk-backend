package ru.kubsu.borshchevyk.calls.application.port.in;

import ru.kubsu.borshchevyk.calls.application.dto.command.InitiateCallCommand;
import ru.kubsu.borshchevyk.calls.application.dto.command.JoinCallCommand;
import ru.kubsu.borshchevyk.calls.domain.model.Call;

/**
 * Inbound port for managing call lifecycles.
 *
 * @author Gemini
 * @since 2026-04-25
 */
public interface ManageCallUseCase {
    Call initiateCall(InitiateCallCommand command);
    String joinCall(JoinCallCommand command);
}
