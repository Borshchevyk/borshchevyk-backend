package ru.kubsu.borshchevyk.calls.application.dto.command;

import ru.kubsu.borshchevyk.calls.domain.model.UserId;
import java.util.Set;

/**
 * Command to initiate a new call.
 *
 * @author Gemini
 * @since 2026-04-25
 */
public record InitiateCallCommand(
        UserId initiatorId,
        Set<UserId> participantIds
) {}
