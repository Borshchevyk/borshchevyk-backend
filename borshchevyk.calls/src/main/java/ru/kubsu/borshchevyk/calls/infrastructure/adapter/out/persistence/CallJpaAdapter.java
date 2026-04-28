package ru.kubsu.borshchevyk.calls.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.calls.application.port.out.LoadCallPort;
import ru.kubsu.borshchevyk.calls.application.port.out.SaveCallPort;
import ru.kubsu.borshchevyk.calls.domain.model.Call;
import ru.kubsu.borshchevyk.calls.domain.model.CallId;

import java.util.Optional;

/**
 * JPA implementation of SaveCallPort and LoadCallPort.
 *
 * @author Aleksey Timko
 * @since 2026-04-25
 */
@Component
@RequiredArgsConstructor
public class CallJpaAdapter implements LoadCallPort, SaveCallPort {
    private final CallRepository repository;
    private final CallEntityMapper mapper;

    @Override
    public Optional<Call> loadCall(CallId callId) {
        return repository.findById(callId.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<Call> loadCallByRoomId(String roomId) {
        return repository.findByRoomId(roomId).map(mapper::toDomain);
    }

    @Override
    public Call saveCall(Call call) {
        CallEntity entity = mapper.toEntity(call);
        CallEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }
}
