package ru.kubsu.borshchevyk.calls.domain.event.call;

import ru.kubsu.borshchevyk.calls.domain.model.Call;

import java.util.Set;
import java.util.UUID;

public record CallEndedEvent(Call call) implements CallEvent {

    @Override
    public CallEventType type() {
        return CallEventType.INITIATED;
    }

    @Override
    public UUID callId() {
        return call.getId().value();
    }

    @Override
    public UUID actorId() {
        return call.getInitiatorId().value();
    }

    @Override
    public UUID initiatorId() {
        return call.getInitiatorId().value();
    }

    @Override
    public Set<UUID> participants() {
        return call.getParticipantsUUIDs();
    }
}