package ru.kubsu.borshchevyk.sync.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.sync.application.port.out.SyncEventPort;
import ru.kubsu.borshchevyk.sync.domain.model.SyncEvent;
import ru.kubsu.borshchevyk.sync.infrastructure.adapter.out.persistence.entity.SyncEventEntity;
import ru.kubsu.borshchevyk.sync.infrastructure.adapter.out.persistence.mapper.SyncEventMapper;
import ru.kubsu.borshchevyk.sync.infrastructure.adapter.out.persistence.repository.SyncEventRepository;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SyncEventAdapter implements SyncEventPort {

    private final SyncEventRepository repository;
    private final SyncEventMapper mapper;

    @Override
    @Transactional
    public void save(SyncEvent event) {
        Long latestSeq = getLatestSequence(event.getTargetUserId());
        event.setSequenceNumber(latestSeq + 1);
        SyncEventEntity entity = mapper.toEntity(event);
        repository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SyncEvent> loadAfterSequence(UUID targetUserId, Long sequenceNumber, int limit) {
        List<SyncEventEntity> entities = repository.findByTargetUserIdAndSequenceNumberGreaterThanOrderBySequenceNumberAsc(
                targetUserId, sequenceNumber, PageRequest.of(0, limit));
        return mapper.toDomainList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getLatestSequence(UUID targetUserId) {
        return repository.findFirstByTargetUserIdOrderBySequenceNumberDesc(targetUserId)
                .map(SyncEventEntity::getSequenceNumber)
                .orElse(0L);
    }
}