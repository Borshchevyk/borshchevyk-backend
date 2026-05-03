package ru.kubsu.borshchevyk.calls.application.port.out;

import ru.kubsu.borshchevyk.calls.domain.event.call.CallEvent;

/**
 * Port for publishing domain events related to calls.
 */
public interface PublishCallEventPort {
    void publish(CallEvent event);
}