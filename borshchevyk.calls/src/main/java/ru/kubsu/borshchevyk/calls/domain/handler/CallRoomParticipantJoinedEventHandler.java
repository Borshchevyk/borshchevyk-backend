package ru.kubsu.borshchevyk.calls.domain.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.calls.application.port.out.LoadCallByRoomIdPort;
import ru.kubsu.borshchevyk.calls.application.port.out.LoadCallPort;
import ru.kubsu.borshchevyk.calls.application.port.out.SaveCallPort;
import ru.kubsu.borshchevyk.calls.domain.event.call_room.CallRoomEvent;
import ru.kubsu.borshchevyk.calls.domain.event.call_room.CallRoomParticipantJoinedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class CallRoomParticipantJoinedEventHandler implements CallRoomEventHandler<CallRoomParticipantJoinedEvent> {

    private final LoadCallByRoomIdPort loadCallByRoomIdPort;
    private final SaveCallPort saveCallPort;

    @Override
    public boolean canHandle(CallRoomEvent event) {
        return event instanceof CallRoomParticipantJoinedEvent;
    }

    @Override
    @Transactional
    public void handle(CallRoomParticipantJoinedEvent event) {
        loadCallByRoomIdPort.loadCallByRoomId(event.roomId()).ifPresent(call -> {
            call.markAsInProgress();
            saveCallPort.saveCall(call);
            log.info("Call {} marked as IN_PROGRESS due to participant {} joining", call.getId().value(), event.participantIdentity());
        });
    }
}
