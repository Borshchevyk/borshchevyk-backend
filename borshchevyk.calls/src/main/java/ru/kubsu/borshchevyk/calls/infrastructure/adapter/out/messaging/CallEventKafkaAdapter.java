package ru.kubsu.borshchevyk.calls.infrastructure.adapter.out.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.calls.application.port.out.PublishCallEventPort;
import ru.kubsu.borshchevyk.calls.domain.model.Call;

/**
 * Kafka implementation for publishing call events.
 *
 * @author Aleksey Timko
 * @since 2026-04-25
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CallEventKafkaAdapter implements PublishCallEventPort {

    @Override
    public void publishCallInitiated(Call call) {
        log.info("Publishing CallInitiated event for call {}", call.getId().value());
        // TODO: implement actual kafka publishing when Event DTO is defined
    }

    @Override
    public void publishCallEnded(Call call) {
        log.info("Publishing CallEnded event for call {}", call.getId().value());
        // TODO: implement actual kafka publishing when Event DTO is defined
    }
}
