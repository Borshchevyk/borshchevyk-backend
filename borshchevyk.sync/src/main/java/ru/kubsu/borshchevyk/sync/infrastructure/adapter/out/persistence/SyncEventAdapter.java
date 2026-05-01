package ru.kubsu.borshchevyk.sync.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

/**
 * Persistence adapter for Sync Events.
 *
 * @author Aleksey Timko
 * @since 2026-03-01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SyncEventAdapter implements SyncEventPort {

    private final SyncEventRepository repository;
    private final SyncEventMapper mapper;

    @Override
    @Transactional
    public void save(SyncEvent event) {
        log.debug("Saving sync event for target user: {}", event.targetUserId());
        Long latestSeq = getLatestSequence(event.targetUserId());
        SyncEvent eventWithSeq = new SyncEvent(
                event.eventId(),
                event.targetUserId(),
                latestSeq + 1,
                event.eventType(),
                event.payload(),
                event.createdAt()
        );
        SyncEventEntity entity = mapper.toEntity(eventWithSeq);
        repository.save(entity);
        log.debug("Saved sync event with sequence number: {}", eventWithSeq.sequenceNumber());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SyncEvent> loadAfterSequence(UUID targetUserId, Long sequenceNumber, int limit) {
        log.debug("Loading events for user {} after sequence {} with limit {}", targetUserId, sequenceNumber, limit);
        List<SyncEventEntity> entities = repository.findByTargetUserIdAndSequenceNumberGreaterThanOrderBySequenceNumberAsc(
                targetUserId, sequenceNumber, PageRequest.of(0, limit));
        return mapper.toDomainList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getLatestSequence(UUID targetUserId) {
        log.debug("Getting latest sequence for user: {}", targetUserId);
        return repository.findFirstByTargetUserIdOrderBySequenceNumberDesc(targetUserId)
                .map(SyncEventEntity::getSequenceNumber)
                .orElse(0L);
    }
}
