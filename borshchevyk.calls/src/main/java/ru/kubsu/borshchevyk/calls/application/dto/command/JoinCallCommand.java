package ru.kubsu.borshchevyk.calls.application.dto.command;

import jakarta.validation.constraints.NotNull;
import ru.kubsu.borshchevyk.calls.domain.value.CallId;
import ru.kubsu.borshchevyk.calls.domain.value.UserId;

/**
 * Command to join an existing call.
 */
public record JoinCallCommand(
        @NotNull(message = "Call ID cannot be null")
        CallId callId,
        @NotNull(message = "User ID cannot be null")
        UserId userId
) {}
