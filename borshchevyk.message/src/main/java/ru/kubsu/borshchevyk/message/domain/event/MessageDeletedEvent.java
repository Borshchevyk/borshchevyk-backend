package ru.kubsu.borshchevyk.message.domain.event;

import java.util.List;
import java.util.UUID;

/**
 * Domain event published when a message is deleted.
 *
 * @author Aleksey Timko
 */
public record MessageDeletedEvent(
        UUID messageId,
        UUID chatId,
        List<String> targetUserIds
) {}
