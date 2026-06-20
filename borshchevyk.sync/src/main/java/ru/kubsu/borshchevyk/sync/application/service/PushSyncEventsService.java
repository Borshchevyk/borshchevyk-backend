package ru.kubsu.borshchevyk.sync.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.sync.application.dto.command.PushSyncEventsCommand;
import ru.kubsu.borshchevyk.sync.application.port.in.PushSyncEventsUseCase;
import ru.kubsu.borshchevyk.sync.application.port.out.BroadcastSyncEventPort;
import ru.kubsu.borshchevyk.sync.application.port.out.LoadSyncEventsPort;
import ru.kubsu.borshchevyk.sync.application.port.out.PublishSyncMutationPort;
import ru.kubsu.borshchevyk.sync.application.port.out.SaveSyncEventPort;
import ru.kubsu.borshchevyk.sync.domain.model.SyncEvent;
import ru.kubsu.borshchevyk.sync.domain.model.VectorClock;

/**
 * Service implementing the use case for pushing offline synchronization events.
 *
 * @author Aleksey Timko
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PushSyncEventsService implements PushSyncEventsUseCase {

    private final LoadSyncEventsPort loadSyncEventsPort;
    private final SaveSyncEventPort saveSyncEventPort;
    private final BroadcastSyncEventPort broadcastSyncEventPort;
    private final PublishSyncMutationPort publishSyncMutationPort;

    @Override
    @Transactional
    public VectorClock push(PushSyncEventsCommand command) {
        VectorClock currentServerClock = loadSyncEventsPort.loadAndLockCurrentServerClock();

        for (SyncEvent event : command.clientEvents()) {
            VectorClock updatedServerClock = currentServerClock.merge(event.vectorClock()).increment("server");
            currentServerClock = updatedServerClock;

            java.util.Map<String, Long> eventClocks = new java.util.HashMap<>(event.vectorClock().getClocks());
            eventClocks.put("server", updatedServerClock.getClocks().get("server"));
            VectorClock savedEventClock = new VectorClock(eventClocks);

            SyncEvent savedEvent = new SyncEvent(
                    event.id(),
                    event.entityId(),
                    event.userId(),
                    event.eventType(),
                    event.payload(),
                    savedEventClock,
                    event.timestamp()
            );

            saveSyncEventPort.save(savedEvent);
            broadcastSyncEventPort.broadcast(savedEvent);
            publishSyncMutationPort.publish(savedEvent);
            
            log.info("Pushed client event: {} with new clock {}", savedEvent.id(), savedEventClock);
        }

        saveSyncEventPort.saveCurrentServerClock(currentServerClock);

        return currentServerClock;
    }
}
