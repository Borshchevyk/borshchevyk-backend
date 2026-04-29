package ru.kubsu.borshchevyk.calls.application.port.out;

import ru.kubsu.borshchevyk.calls.domain.model.Call;
import ru.kubsu.borshchevyk.calls.domain.model.UserId;

/**
 * Port for publishing domain events related to calls (e.g., to Kafka).
 *
 * @author Aleksey Timko
 * @since 2026-04-25
 */
public interface PublishCallEventPort {
    void publishCallInitiated(Call call);
    void publishCallEnded(Call call);
    void publishCallAccepted(Call call, UserId userId);
    void publishCallRejected(Call call, UserId userId);
}
