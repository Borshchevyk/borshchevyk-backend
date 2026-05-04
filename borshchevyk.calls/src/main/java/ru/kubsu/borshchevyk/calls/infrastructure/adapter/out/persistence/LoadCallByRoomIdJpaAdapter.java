package ru.kubsu.borshchevyk.calls.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.calls.application.port.out.LoadCallByRoomIdPort;
import ru.kubsu.borshchevyk.calls.application.port.out.LoadCallPort;
import ru.kubsu.borshchevyk.calls.application.port.out.SaveCallPort;
import ru.kubsu.borshchevyk.calls.domain.model.Call;
import ru.kubsu.borshchevyk.calls.domain.value.CallId;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.out.persistence.entity.CallJpaEntity;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.out.persistence.mapper.CallMapper;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.out.persistence.repository.CallJpaRepository;

import java.util.Optional;

/**
 * JPA implementation of SaveCallPort and LoadCallPort.
 */
@Component
@RequiredArgsConstructor
public class LoadCallByRoomIdJpaAdapter implements LoadCallByRoomIdPort {

    private final CallJpaRepository repository;
    private final CallMapper mapper;

    @Override
    public Optional<Call> loadCallByRoomId(String roomId) {
        return repository.findByRoomId(roomId).map(mapper::toDomain);
    }
}
