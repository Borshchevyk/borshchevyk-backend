package ru.kubsu.borshchevyk.sync.application.dto.command;

import lombok.Builder;

import java.util.UUID;

/**
 * Command to pull events for synchronization.
 *
 * @author Aleksey Timko
 */
@Builder
public record PullEventsCommand(
        UUID requesterId,
        String syncToken,
        int limit
) {
}
