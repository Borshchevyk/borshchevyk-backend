package ru.kubsu.borshchevyk.calls.application.dto.command;

import jakarta.validation.constraints.NotNull;
import ru.kubsu.borshchevyk.calls.domain.model.CallId;
import ru.kubsu.borshchevyk.calls.domain.model.UserId;

/**
 * Command to end an existing call.
 *
 * @author Aleksey Timko
 * @since 2026-04-25
 */
public record EndCallCommand(
        @NotNull(message = "Call ID cannot be null")
        CallId callId,
        @NotNull(message = "User ID cannot be null")
        UserId userId
) {}
