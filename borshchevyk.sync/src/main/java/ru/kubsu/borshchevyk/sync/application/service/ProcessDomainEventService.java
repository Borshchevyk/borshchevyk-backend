package ru.kubsu.borshchevyk.sync.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.sync.application.dto.command.ProcessDomainEventCommand;
import ru.kubsu.borshchevyk.sync.application.port.in.ProcessDomainEventUseCase;
import ru.kubsu.borshchevyk.sync.application.port.out.BroadcastSyncEventPort;
import ru.kubsu.borshchevyk.sync.application.port.out.LoadSyncEventsPort;
import ru.kubsu.borshchevyk.sync.application.port.out.SaveSyncEventPort;
import ru.kubsu.borshchevyk.sync.domain.model.SyncEvent;
import ru.kubsu.borshchevyk.sync.domain.model.VectorClock;

import java.time.Instant;
import java.util.UUID;

/**
 * Service for processing incoming domain events from other microservices via Kafka.
 *
 * @author Aleksey Timko
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessDomainEventService implements ProcessDomainEventUseCase {

    private final LoadSyncEventsPort loadSyncEventsPort;
    private final SaveSyncEventPort saveSyncEventPort;
    private final BroadcastSyncEventPort broadcastSyncEventPort;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Override
    @Transactional
    public void process(ProcessDomainEventCommand command) {
        VectorClock currentServerClock = loadSyncEventsPort.loadAndLockCurrentServerClock();
        VectorClock nextClock;
        VectorClock eventClock;

        String payload = command.payload();
        if (payload != null && payload.contains("vectorClock")) {
            try {
                com.fasterxml.jackson.databind.JsonNode rootNode = objectMapper.readTree(payload);
                com.fasterxml.jackson.databind.JsonNode clockNode = rootNode.get("vectorClock");
                if (clockNode != null && !clockNode.isNull()) {
                    VectorClock clientClock = objectMapper.treeToValue(clockNode, VectorClock.class);
                    nextClock = currentServerClock.merge(clientClock).increment("server");
                    
                    Long newServerClockValue = nextClock.getClocks().get("server");
                    java.util.Map<String, Long> clientClocksMap = new java.util.HashMap<>(clientClock.getClocks());
                    clientClocksMap.put("server", newServerClockValue);
                    eventClock = new VectorClock(clientClocksMap);
                } else {
                    nextClock = currentServerClock.increment("server");
                    eventClock = nextClock;
                }
            } catch (Exception e) {
                log.error("Failed to parse vector clock from payload: {}", payload, e);
                nextClock = currentServerClock.increment("server");
                eventClock = nextClock;
            }
        } else {
            nextClock = currentServerClock.increment("server");
            eventClock = nextClock;
        }

        SyncEvent event = new SyncEvent(
                UUID.randomUUID(),
                command.entityId(),
                command.userId(),
                command.type(),
                command.payload(),
                eventClock,
                Instant.now()
        );

        saveSyncEventPort.save(event);
        saveSyncEventPort.saveCurrentServerClock(nextClock); // <-- Save updated clock!
        broadcastSyncEventPort.broadcast(event);
        
        log.info("Processed domain event {} for entity {}, vector clock: {}", command.type(), command.entityId(), eventClock);
    }
}
