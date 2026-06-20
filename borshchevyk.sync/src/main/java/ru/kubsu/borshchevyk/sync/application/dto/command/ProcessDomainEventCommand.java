package ru.kubsu.borshchevyk.sync.application.dto.command;

import ru.kubsu.borshchevyk.sync.domain.model.EventType;

import java.util.UUID;

/**
 * Command object for processing incoming domain events.
 *
 * @author Aleksey Timko
 */
public record ProcessDomainEventCommand(
        UUID entityId,
        UUID userId,
        EventType type,
        String payload
) {
}
