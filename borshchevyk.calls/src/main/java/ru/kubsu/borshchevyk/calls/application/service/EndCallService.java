package ru.kubsu.borshchevyk.calls.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.kubsu.borshchevyk.calls.application.dto.command.EndCallCommand;
import ru.kubsu.borshchevyk.calls.application.port.in.EndCallUseCase;
import ru.kubsu.borshchevyk.calls.application.port.out.LoadCallPort;
import ru.kubsu.borshchevyk.calls.application.port.out.PublishCallEventPort;
import ru.kubsu.borshchevyk.calls.application.port.out.SaveCallPort;
import ru.kubsu.borshchevyk.calls.domain.event.call.CallEndedEvent;
import ru.kubsu.borshchevyk.calls.domain.event.call.CallEvent;
import ru.kubsu.borshchevyk.calls.domain.exception.CallNotFoundException;
import ru.kubsu.borshchevyk.calls.domain.exception.UserForbiddenException;
import ru.kubsu.borshchevyk.calls.domain.model.Call;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class EndCallService implements EndCallUseCase {

    private final SaveCallPort saveCallPort;
    private final LoadCallPort loadCallPort;
    private final PublishCallEventPort publishCallEventPort;

    @Override
    @Transactional
    public Call endCall(EndCallCommand command) {
        log.info("User {} ending call {}", command.userId(), command.callId());

        Call call = loadCallPort.loadCall(command.callId())
                .orElseThrow(CallNotFoundException::new);

        if (!call.getInitiatorId().equals(command.userId())) {
            throw new UserForbiddenException();
        }

        call.endCall();
        Call savedCall = saveCallPort.saveCall(call);

        CallEvent event = new CallEndedEvent(savedCall);
        publishCallEventPort.publish(event);

        return savedCall;
    }
}
