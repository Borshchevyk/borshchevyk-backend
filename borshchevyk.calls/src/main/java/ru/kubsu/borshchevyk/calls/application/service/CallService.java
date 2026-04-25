package ru.kubsu.borshchevyk.calls.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.calls.application.dto.command.InitiateCallCommand;
import ru.kubsu.borshchevyk.calls.application.dto.command.JoinCallCommand;
import ru.kubsu.borshchevyk.calls.application.port.in.ManageCallUseCase;
import ru.kubsu.borshchevyk.calls.application.port.out.LiveKitPort;
import ru.kubsu.borshchevyk.calls.application.port.out.LoadCallPort;
import ru.kubsu.borshchevyk.calls.application.port.out.PublishCallEventPort;
import ru.kubsu.borshchevyk.calls.application.port.out.SaveCallPort;
import ru.kubsu.borshchevyk.calls.domain.exception.CallNotFoundException;
import ru.kubsu.borshchevyk.calls.domain.model.Call;
import ru.kubsu.borshchevyk.calls.domain.model.CallId;
import ru.kubsu.borshchevyk.calls.domain.model.CallStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * Service implementing Call lifecycle management use cases.
 *
 * @author Gemini
 * @since 2026-04-25
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CallService implements ManageCallUseCase {

    private final SaveCallPort saveCallPort;
    private final LoadCallPort loadCallPort;
    private final LiveKitPort liveKitPort;
    private final PublishCallEventPort publishCallEventPort;

    @Override
    @Transactional
    public Call initiateCall(InitiateCallCommand command) {
        log.info("Initiating call by user: {}", command.initiatorId());

        String roomId = "room-" + UUID.randomUUID().toString();
        
        Call call = Call.builder()
                .id(CallId.generate())
                .roomId(roomId)
                .initiatorId(command.initiatorId())
                .status(CallStatus.INITIATED)
                .createdAt(Instant.now())
                .build();

        call.addParticipant(command.initiatorId());
        if (command.participantIds() != null) {
            command.participantIds().forEach(call::addParticipant);
        }

        Call savedCall = saveCallPort.saveCall(call);
        
        // Notify other participants via Kafka
        publishCallEventPort.publishCallInitiated(savedCall);

        return savedCall;
    }

    @Override
    @Transactional(readOnly = true)
    public String joinCall(JoinCallCommand command) {
        log.info("User {} joining call {}", command.userId(), command.callId());

        Call call = loadCallPort.loadCall(command.callId())
                .orElseThrow(() -> new CallNotFoundException("Call not found with id: " + command.callId().value()));

        if (call.getStatus() == CallStatus.ENDED) {
            throw new IllegalStateException("Cannot join an ended call");
        }
        
        // Only allow participants to join (or we can auto-add them, depending on business rules)
        if (!call.getParticipants().contains(command.userId())) {
            throw new IllegalArgumentException("User is not a participant of this call");
        }

        // Generate token
        return liveKitPort.generateJoinToken(call.getRoomId(), command.userId(), true);
    }
}
