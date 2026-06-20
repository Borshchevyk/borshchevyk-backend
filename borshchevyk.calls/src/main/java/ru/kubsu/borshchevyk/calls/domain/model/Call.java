package ru.kubsu.borshchevyk.calls.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import ru.kubsu.borshchevyk.calls.domain.value.CallId;
import ru.kubsu.borshchevyk.calls.domain.value.CallStatus;
import ru.kubsu.borshchevyk.calls.domain.value.UserId;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Aggregate Root representing a Call.
 */
@Getter
@Builder(toBuilder = true)
public class Call {
    private final CallId id;
    private final String roomId;
    @NonNull
    private final UserId initiatorId;
    private CallStatus status;
    private final Instant createdAt;
    private Instant endedAt;

    @Builder.Default
    private final Set<UserId> participants = new HashSet<>();

    public void addParticipant(UserId userId) {
        this.participants.add(userId);
    }

    public void removeParticipant(UserId userId) {
        this.participants.remove(userId);
    }

    public Set<UUID> getParticipantsUUIDs() {
        return participants.stream().map(UserId::value).collect(Collectors.toSet());
    }

    public void markAsInProgress() {
        if (this.status == CallStatus.INITIATED || this.status == CallStatus.RINGING) {
            this.status = CallStatus.IN_PROGRESS;
        }
    }

    public void endCall() {
        if (this.status != CallStatus.ENDED) {
            this.status = CallStatus.ENDED;
            this.endedAt = Instant.now();
        }
    }
}
