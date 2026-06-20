package ru.kubsu.borshchevyk.calls.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.calls.application.port.out.SaveCallPort;
import ru.kubsu.borshchevyk.calls.domain.model.Call;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.out.persistence.entity.CallJpaEntity;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.out.persistence.mapper.CallJpaMapper;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.out.persistence.repository.CallJpaRepository;

/**
 * JPA implementation of SaveCallPort and LoadCallPort.
 */
@Component
@RequiredArgsConstructor
public class SaveCallJpaAdapter implements SaveCallPort {

    private final CallJpaRepository repository;
    private final CallJpaMapper mapper;

    @Override
    public Call saveCall(Call call) {
        CallJpaEntity entity = mapper.toEntity(call);
        CallJpaEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }
}