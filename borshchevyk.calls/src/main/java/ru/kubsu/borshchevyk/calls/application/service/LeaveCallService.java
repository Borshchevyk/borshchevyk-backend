package ru.kubsu.borshchevyk.calls.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.kubsu.borshchevyk.calls.application.dto.command.LeaveCallCommand;
import ru.kubsu.borshchevyk.calls.application.port.in.LeaveCallUseCase;
import ru.kubsu.borshchevyk.calls.application.port.out.LoadCallPort;
import ru.kubsu.borshchevyk.calls.application.port.out.PublishCallEventPort;
import ru.kubsu.borshchevyk.calls.application.port.out.SaveCallPort;
import ru.kubsu.borshchevyk.calls.domain.event.call.CallEvent;
import ru.kubsu.borshchevyk.calls.domain.event.call.CallRejectedEvent;
import ru.kubsu.borshchevyk.calls.domain.exception.CallNotFoundException;
import ru.kubsu.borshchevyk.calls.domain.model.Call;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class LeaveCallService implements LeaveCallUseCase {

    private final SaveCallPort saveCallPort;
    private final LoadCallPort loadCallPort;
    private final PublishCallEventPort publishCallEventPort;

    @Override
    @Transactional
    public Call leaveCall(LeaveCallCommand command) {
        log.info("User {} leaving call {}", command.userId(), command.callId());

        Call call = loadCallPort.loadCall(command.callId())
                .orElseThrow(CallNotFoundException::new);

        if (!command.isSyncMutation()) {
            CallEvent event = new CallRejectedEvent(call, command.userId());
            publishCallEventPort.publish(event);
        }

        call.removeParticipant(command.userId());
        return saveCallPort.saveCall(call);
    }
}
