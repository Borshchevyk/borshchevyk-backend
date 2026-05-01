package ru.kubsu.borshchevyk.sync.application.port.in;

import ru.kubsu.borshchevyk.sync.application.dto.command.PullEventsCommand;
import ru.kubsu.borshchevyk.sync.application.dto.result.PullResult;

/**
 * Use case for pulling synchronization events.
 *
 * @author Aleksey Timko
 * @since 2026-03-01
 */
public interface PullEventsUseCase {
    PullResult pull(PullEventsCommand command);
}
