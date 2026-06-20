package ru.kubsu.borshchevyk.calls.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.kubsu.borshchevyk.calls.application.dto.command.JoinCallCommand;
import ru.kubsu.borshchevyk.calls.application.port.in.JoinCallUseCase;
import ru.kubsu.borshchevyk.calls.application.port.out.LiveKitPort;
import ru.kubsu.borshchevyk.calls.application.port.out.LoadCallPort;
import ru.kubsu.borshchevyk.calls.application.port.out.PublishCallEventPort;
import ru.kubsu.borshchevyk.calls.domain.event.call.CallAcceptedEvent;
import ru.kubsu.borshchevyk.calls.domain.event.call.CallEvent;
import ru.kubsu.borshchevyk.calls.domain.exception.CallEndedException;
import ru.kubsu.borshchevyk.calls.domain.exception.CallNotFoundException;
import ru.kubsu.borshchevyk.calls.domain.exception.UserForbiddenException;
import ru.kubsu.borshchevyk.calls.domain.model.Call;
import ru.kubsu.borshchevyk.calls.domain.value.CallStatus;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class JoinCallService implements JoinCallUseCase {

    private final LoadCallPort loadCallPort;
    private final LiveKitPort liveKitPort;
    private final PublishCallEventPort publishCallEventPort;

    @Override
    @Transactional(readOnly = true)
    public String joinCall(JoinCallCommand command) {
        log.info("User {} joining call {}", command.userId(), command.callId());

        Call call = loadCallPort.loadCall(command.callId())
                .orElseThrow(CallNotFoundException::new);

        if (call.getStatus() == CallStatus.ENDED) {
            throw new CallEndedException();
        }

        if (!call.getParticipants().contains(command.userId())) {
            throw new UserForbiddenException();
        }

        if (!command.isSyncMutation()) {
            CallEvent event = new CallAcceptedEvent(call, command.userId());
            publishCallEventPort.publish(event);
        }

        return liveKitPort.generateJoinToken(call.getRoomId(), command.userId(), true);
    }
}