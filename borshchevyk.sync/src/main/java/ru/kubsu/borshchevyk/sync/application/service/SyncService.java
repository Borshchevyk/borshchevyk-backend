package ru.kubsu.borshchevyk.sync.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.sync.application.dto.command.PullEventsCommand;
import ru.kubsu.borshchevyk.sync.application.dto.result.PullResult;
import ru.kubsu.borshchevyk.sync.application.port.in.ProcessIncomingEventUseCase;
import ru.kubsu.borshchevyk.sync.application.port.in.PullEventsUseCase;
import ru.kubsu.borshchevyk.sync.application.port.out.SyncEventPort;
import ru.kubsu.borshchevyk.sync.domain.model.EventType;
import ru.kubsu.borshchevyk.sync.domain.model.SyncEvent;
import ru.kubsu.borshchevyk.sync.domain.model.SyncToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service handling the core synchronization logic.
 *
 * @author Aleksey Timko
 * @since 2026-03-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SyncService implements PullEventsUseCase, ProcessIncomingEventUseCase {

    private final SyncEventPort syncEventPort;

    @Override
    public PullResult pull(PullEventsCommand command) {
        log.info("Pulling events for user: {}, limit: {}", command.requesterId(), command.limit());
        
        SyncToken token = new SyncToken(command.syncToken());
        Long sequenceNumber = token.decode();
        
        // Fetch requested limit + 1 to know if there are more items
        int limit = command.limit() > 0 ? command.limit() : 50;
        List<SyncEvent> events = syncEventPort.loadAfterSequence(command.requesterId(), sequenceNumber, limit + 1);
        
        boolean hasMore = events.size() > limit;
        if (hasMore) {
            events = events.subList(0, limit);
        }
        
        Long lastSequenceNumber = sequenceNumber;
        if (!events.isEmpty()) {
            lastSequenceNumber = events.get(events.size() - 1).sequenceNumber();
        }
        
        String nextToken = SyncToken.encode(lastSequenceNumber).value();
        
        log.debug("Found {} events for user: {}. hasMore: {}", events.size(), command.requesterId(), hasMore);
        
        return PullResult.builder()
                .events(events)
                .nextToken(nextToken)
                .hasMore(hasMore)
                .build();
    }

    @Override
    public void process(UUID targetUserId, EventType type, String payload) {
        log.debug("Processing incoming event of type {} for target user: {}", type, targetUserId);
        
        SyncEvent event = SyncEvent.builder()
                .eventId(UUID.randomUUID())
                .targetUserId(targetUserId)
                .eventType(type)
                .payload(payload)
                .createdAt(LocalDateTime.now())
                .build();
                
        syncEventPort.save(event);
        log.info("Successfully saved sync event {} for user {}", event.eventId(), targetUserId);
    }
}
