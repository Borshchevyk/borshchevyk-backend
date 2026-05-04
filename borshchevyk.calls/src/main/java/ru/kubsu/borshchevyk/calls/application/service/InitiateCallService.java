package ru.kubsu.borshchevyk.calls.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.kubsu.borshchevyk.calls.application.dto.command.InitiateCallCommand;
import ru.kubsu.borshchevyk.calls.application.port.in.InitiateCallUseCase;
import ru.kubsu.borshchevyk.calls.application.port.out.PublishCallEventPort;
import ru.kubsu.borshchevyk.calls.application.port.out.SaveCallPort;
import ru.kubsu.borshchevyk.calls.domain.event.call.CallEvent;
import ru.kubsu.borshchevyk.calls.domain.event.call.CallInitiatedEvent;
import ru.kubsu.borshchevyk.calls.domain.model.Call;
import ru.kubsu.borshchevyk.calls.domain.value.CallId;
import ru.kubsu.borshchevyk.calls.domain.value.CallStatus;

import java.time.Instant;
import java.util.UUID;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class InitiateCallService implements InitiateCallUseCase {

    private final PublishCallEventPort publishCallEventPort;
    private final SaveCallPort saveCallPort;

    @Override
    @Transactional
    public Call initiateCall(InitiateCallCommand command) {
        log.info("Initiating call by user: {}", command.initiatorId());

        String roomId = "room-" + UUID.randomUUID();

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

        CallEvent event = new CallInitiatedEvent(savedCall);
        publishCallEventPort.publish(event);

        return savedCall;
    }
}
