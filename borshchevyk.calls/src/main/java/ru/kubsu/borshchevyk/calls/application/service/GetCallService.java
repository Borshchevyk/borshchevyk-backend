package ru.kubsu.borshchevyk.calls.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.kubsu.borshchevyk.calls.application.dto.query.GetCallQuery;
import ru.kubsu.borshchevyk.calls.application.port.in.GetCallUseCase;
import ru.kubsu.borshchevyk.calls.application.port.out.LoadCallPort;
import ru.kubsu.borshchevyk.calls.domain.exception.CallNotFoundException;
import ru.kubsu.borshchevyk.calls.domain.exception.UserForbiddenException;
import ru.kubsu.borshchevyk.calls.domain.model.Call;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class GetCallService implements GetCallUseCase {

    private final LoadCallPort loadCallPort;

    @Override
    @Transactional(readOnly = true)
    public Call getCall(GetCallQuery query) {
        log.info("User {} fetching call {}", query.userId(), query.callId());

        Call call = loadCallPort.loadCall(query.callId())
                .orElseThrow(CallNotFoundException::new);

        if (!call.getParticipants().contains(query.userId()) && !call.getInitiatorId().equals(query.userId())) {
            throw new UserForbiddenException();
        }

        return call;
    }
}
