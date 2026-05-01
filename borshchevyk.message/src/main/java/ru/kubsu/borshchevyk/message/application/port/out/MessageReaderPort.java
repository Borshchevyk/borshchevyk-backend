package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.util.List;

/**
 * Output port for managing read receipts of messages.
 *
 * @author Aleksey Timko
 */
public interface MessageReaderPort {
    void save(MessageId messageId, UserId userId);
    List<UserId> findReaders(MessageId messageId);
}
