package ru.kubsu.borshchevyk.calls.domain.event.call;

import java.util.Set;
import java.util.UUID;

public sealed interface CallEvent permits
        CallInitiatedEvent,
        CallEndedEvent,
        CallAcceptedEvent,
        CallRejectedEvent {

    CallEventType type();
    UUID callId();
    UUID actorId();

    UUID initiatorId();
    Set<UUID> participants();
}