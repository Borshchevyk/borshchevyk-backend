package ru.kubsu.borshchevyk.calls.application.dto.query;

import ru.kubsu.borshchevyk.calls.domain.model.CallId;
import ru.kubsu.borshchevyk.calls.domain.model.UserId;

/**
 * Query to get an existing call by ID.
 *
 * @author Aleksey Timko
 * @since 2026-04-25
 */
public record GetCallQuery(
        CallId callId,
        UserId userId
) {}
