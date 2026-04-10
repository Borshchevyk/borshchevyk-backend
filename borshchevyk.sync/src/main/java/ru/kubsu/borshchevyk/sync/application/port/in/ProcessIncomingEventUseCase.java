package ru.kubsu.borshchevyk.sync.application.port.in;

import ru.kubsu.borshchevyk.sync.domain.model.EventType;

import java.util.UUID;

public interface ProcessIncomingEventUseCase {
    void process(UUID targetUserId, EventType type, String payload);
}
