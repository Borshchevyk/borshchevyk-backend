package ru.kubsu.borshchevyk.sync.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.sync.application.dto.query.PullSyncEventsQuery;
import ru.kubsu.borshchevyk.sync.application.port.in.PullSyncEventsUseCase;
import ru.kubsu.borshchevyk.sync.application.port.out.LoadSyncEventsPort;
import ru.kubsu.borshchevyk.sync.domain.model.SyncEvent;

import java.util.List;

/**
 * Service implementing the use case for pulling synchronization events.
 *
 * @author Aleksey Timko
 */
@Service
@RequiredArgsConstructor
public class PullSyncEventsService implements PullSyncEventsUseCase {

    private final LoadSyncEventsPort loadSyncEventsPort;

    @Override
    public List<SyncEvent> pull(PullSyncEventsQuery query) {
        return loadSyncEventsPort.loadEventsAfterOrConcurrent(query.clientClock(), query.userId(), query.limit());
    }
}
