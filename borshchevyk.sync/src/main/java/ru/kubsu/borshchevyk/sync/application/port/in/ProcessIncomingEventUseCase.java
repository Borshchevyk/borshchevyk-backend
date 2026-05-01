package ru.kubsu.borshchevyk.sync.application.port.in;

import ru.kubsu.borshchevyk.sync.domain.model.EventType;

import java.util.UUID;

/**
 * Use case for processing incoming synchronization events.
 *
 * @author Aleksey Timko
 * @since 2026-03-01
 */
public interface ProcessIncomingEventUseCase {
    void process(UUID targetUserId, EventType type, String payload);
}
