package ru.kubsu.borshchevyk.calls.application.dto.command;

import jakarta.validation.constraints.NotNull;
import ru.kubsu.borshchevyk.calls.domain.value.CallId;
import ru.kubsu.borshchevyk.calls.domain.value.UserId;

/**
 * Command to leave an existing call.
 */
public record LeaveCallCommand(
        @NotNull(message = "Call ID cannot be null")
        CallId callId,
        @NotNull(message = "User ID cannot be null")
        UserId userId,
        boolean isSyncMutation
) {
    public LeaveCallCommand(CallId callId, UserId userId) {
        this(callId, userId, false);
    }
}
