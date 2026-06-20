package ru.kubsu.borshchevyk.calls.domain.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.calls.application.port.out.LoadCallByRoomIdPort;
import ru.kubsu.borshchevyk.calls.application.port.out.PublishCallEventPort;
import ru.kubsu.borshchevyk.calls.application.port.out.SaveCallPort;
import ru.kubsu.borshchevyk.calls.domain.event.call.CallEndedEvent;
import ru.kubsu.borshchevyk.calls.domain.event.call_room.CallRoomEvent;
import ru.kubsu.borshchevyk.calls.domain.event.call_room.CallRoomFinishedEvent;
import ru.kubsu.borshchevyk.calls.domain.value.CallStatus;

@Component
@RequiredArgsConstructor
@Slf4j
public class CallRoomFinishedEventHandler implements CallRoomEventHandler<CallRoomFinishedEvent> {

    private final LoadCallByRoomIdPort loadCallByRoomIdPort;
    private final SaveCallPort saveCallPort;
    private final PublishCallEventPort publishCallEventPort;

    @Override
    public boolean canHandle(CallRoomEvent event) {
        return event instanceof CallRoomFinishedEvent;
    }

    @Override
    @Transactional
    public void handle(CallRoomFinishedEvent event) {
        loadCallByRoomIdPort.loadCallByRoomId(event.roomId()).ifPresent(call -> {
            if (call.getStatus() != CallStatus.ENDED) {
                call.endCall();
                saveCallPort.saveCall(call);

                CallEndedEvent callEndedEvent = new CallEndedEvent(call);
                publishCallEventPort.publish(callEndedEvent);

                log.info("Call {} ended via LiveKit webhook", call.getId().value());
            }
        });
    }
}
