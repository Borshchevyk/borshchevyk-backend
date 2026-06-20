package ru.kubsu.borshchevyk.sync.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.sync.application.port.out.LoadSyncEventsPort;
import ru.kubsu.borshchevyk.sync.application.port.out.SaveSyncEventPort;
import ru.kubsu.borshchevyk.sync.domain.model.SyncEvent;
import ru.kubsu.borshchevyk.sync.domain.model.VectorClock;
import ru.kubsu.borshchevyk.sync.infrastructure.adapter.out.persistence.entity.SyncEventEntity;
import ru.kubsu.borshchevyk.sync.infrastructure.adapter.out.persistence.entity.SyncStateEntity;
import ru.kubsu.borshchevyk.sync.infrastructure.adapter.out.persistence.mapper.SyncEventMapper;
import ru.kubsu.borshchevyk.sync.infrastructure.adapter.out.persistence.repository.SyncEventRepository;
import ru.kubsu.borshchevyk.sync.infrastructure.adapter.out.persistence.repository.SyncStateRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Persistence adapter for saving and loading synchronization events in PostgreSQL.
 *
 * @author Aleksey Timko
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PostgresSyncEventAdapter implements SaveSyncEventPort, LoadSyncEventsPort {

    private final SyncEventRepository repository;
    private final SyncStateRepository stateRepository;
    private final SyncEventMapper mapper;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Override
    @Transactional
    public void save(SyncEvent event) {
        log.debug("Saving sync event {} with vector clock: {}", event.id(), event.vectorClock());
        SyncEventEntity entity = mapper.toEntity(event);
        repository.save(entity);
    }

    @Override
    @Transactional
    public void saveCurrentServerClock(VectorClock clock) {
        SyncStateEntity state = stateRepository.findById(1L).orElse(new SyncStateEntity(1L, clock));
        state.setServerVectorClock(clock);
        stateRepository.save(state);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SyncEvent> loadEventsAfterOrConcurrent(VectorClock clientClock, java.util.UUID userId, int limit) {
        log.debug("Loading events after or concurrent with client clock: {} for user: {}", clientClock, userId);
        
        String clientClockJson;
        try {
            clientClockJson = objectMapper.writeValueAsString(clientClock);
        } catch (Exception e) {
            log.error("Failed to serialize client clock to JSON", e);
            clientClockJson = "{\"clocks\":{}}";
        }

        List<SyncEventEntity> events = repository.findEventsAfterOrConcurrent(
                clientClockJson, userId, org.springframework.data.domain.PageRequest.of(0, limit));
        
        return events.stream()
                .map(mapper::toDomain)
                .filter(event -> event.vectorClock().isAfter(clientClock) || event.vectorClock().isConcurrent(clientClock))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VectorClock loadCurrentServerClock() {
        log.debug("Calculating current server vector clock");
        return stateRepository.findById(1L)
                .map(SyncStateEntity::getServerVectorClock)
                .orElse(new VectorClock());
    }

    @Override
    @Transactional
    public VectorClock loadAndLockCurrentServerClock() {
        log.debug("Loading and locking current server vector clock");
        return stateRepository.findLockedState()
                .map(SyncStateEntity::getServerVectorClock)
                .orElse(new VectorClock());
    }
}
