package ru.kubsu.borshchevyk.calls.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.calls.application.port.in.HandleLiveKitWebhookUseCase;
import ru.kubsu.borshchevyk.calls.application.port.out.LoadCallPort;
import ru.kubsu.borshchevyk.calls.application.port.out.PublishCallEventPort;
import ru.kubsu.borshchevyk.calls.application.port.out.SaveCallPort;
import ru.kubsu.borshchevyk.calls.domain.event.call.CallEndedEvent;
import ru.kubsu.borshchevyk.calls.domain.model.CallStatus;

/**
 * Service implementing LiveKit Webhook handling use cases.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LiveKitWebhookHandlingService implements HandleLiveKitWebhookUseCase {

    private final LoadCallPort loadCallPort;
    private final SaveCallPort saveCallPort;
    private final PublishCallEventPort publishCallEventPort;

    @Override
    @Transactional
    public void handleRoomFinished(String roomId) {
        loadCallPort.loadCallByRoomId(roomId).ifPresent(call -> {
            if (call.getStatus() != CallStatus.ENDED) {
                call.endCall();
                saveCallPort.saveCall(call);

                CallEndedEvent event = new CallEndedEvent(call);
                publishCallEventPort.publish(event);

                log.info("Call {} ended via LiveKit webhook", call.getId().value());
            }
        });
    }

    @Override
    @Transactional
    public void handleParticipantJoined(String roomId, String participantIdentity) {
        loadCallPort.loadCallByRoomId(roomId).ifPresent(call -> {
            call.markAsInProgress();
            saveCallPort.saveCall(call);
            log.info("Call {} marked as IN_PROGRESS due to participant {} joining", call.getId().value(), participantIdentity);
        });
    }

    @Override
    @Transactional
    public void handleParticipantLeft(String roomId, String participantIdentity) {
        log.info("Participant {} left room {} via LiveKit webhook", participantIdentity, roomId);
        // Business logic can be added here if needed
    }
}
