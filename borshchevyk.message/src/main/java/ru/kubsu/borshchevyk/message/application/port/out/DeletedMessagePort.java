package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

/**
 * Output port for managing soft-deleted messages for specific users.
 *
 * @author Aleksey Timko
 */
public interface DeletedMessagePort {
    void save(MessageId messageId, UserId userId);
    boolean isDeletedForUser(MessageId messageId, UserId userId);
}
