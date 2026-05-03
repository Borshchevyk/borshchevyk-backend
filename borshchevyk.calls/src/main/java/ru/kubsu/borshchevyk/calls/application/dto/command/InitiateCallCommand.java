package ru.kubsu.borshchevyk.calls.application.dto.command;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import ru.kubsu.borshchevyk.calls.domain.model.UserId;
import java.util.Set;

/**
 * Command to initiate a new call.
 */
public record InitiateCallCommand(
        @NotNull(message = "Initiator ID cannot be null")
        UserId initiatorId,
        @NotEmpty(message = "Participant IDs cannot be empty")
        Set<UserId> participantIds
) {}
