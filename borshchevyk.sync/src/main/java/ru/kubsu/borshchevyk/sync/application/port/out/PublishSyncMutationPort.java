package ru.kubsu.borshchevyk.sync.application.port.out;

import ru.kubsu.borshchevyk.sync.domain.model.SyncEvent;

/**
 * Outbound port for publishing client offline mutations back to the domain microservices.
 *
 * @author Aleksey Timko
 */
public interface PublishSyncMutationPort {
    void publish(SyncEvent event);
}
