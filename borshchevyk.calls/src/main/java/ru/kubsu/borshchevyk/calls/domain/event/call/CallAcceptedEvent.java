package ru.kubsu.borshchevyk.calls.domain.event.call;

import ru.kubsu.borshchevyk.calls.domain.model.Call;
import ru.kubsu.borshchevyk.calls.domain.value.UserId;

import java.util.Set;
import java.util.UUID;

public record CallAcceptedEvent(Call call, UserId userId) implements CallEvent {

    @Override
    public CallEventType type() {
        return CallEventType.ACCEPTED;
    }

    @Override
    public UUID callId() {
        return call.getId().value();
    }

    @Override
    public UUID actorId() {
        return userId.value();
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
