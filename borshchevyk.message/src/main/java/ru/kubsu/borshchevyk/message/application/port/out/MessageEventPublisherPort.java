package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import java.util.List;

/**
 * Output port for publishing message events to external systems (e.g., Kafka).
 *
 * @author Aleksey Timko
 */
public interface MessageEventPublisherPort {
    void publishMessageCreatedEvent(Message message, List<String> targetUserIds);
    void publishMessageDeletedEvent(Message message, List<String> targetUserIds);
}
