package ru.kubsu.borshchevyk.calls.application.dto.command;

import ru.kubsu.borshchevyk.calls.domain.model.CallId;
import ru.kubsu.borshchevyk.calls.domain.model.UserId;

/**
 * Command to join an existing call.
 *
 * @author Aleksey Timko
 * @since 2026-04-25
 */
public record JoinCallCommand(
        CallId callId,
        UserId userId
) {}
