package ru.kubsu.borshchevyk.calls.domain.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.calls.application.port.out.LoadCallPort;
import ru.kubsu.borshchevyk.calls.application.port.out.PublishCallEventPort;
import ru.kubsu.borshchevyk.calls.application.port.out.SaveCallPort;
import ru.kubsu.borshchevyk.calls.domain.event.call_room.CallRoomEvent;
import ru.kubsu.borshchevyk.calls.domain.event.call_room.CallRoomParticipantLeftEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class CallRoomParticipantLeftEventHandler implements CallRoomEventHandler<CallRoomParticipantLeftEvent> {

    private final LoadCallPort loadCallPort;
    private final SaveCallPort saveCallPort;
    private final PublishCallEventPort publishCallEventPort;

    @Override
    public boolean canHandle(CallRoomEvent event) {
        return event instanceof CallRoomParticipantLeftEvent;
    }

    @Override
    @Transactional
    public void handle(CallRoomParticipantLeftEvent event) {
        log.info("Participant {} left room {} via LiveKit webhook", event.participantIdentity(), event.roomId());
    }
}