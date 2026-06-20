package ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.messaging.dto;

/**
 * Event received when a message is deleted.
 *
 * @author Aleksey Timko
 */
public record MessageDeletedEvent(
        String messageId,
        String deletedBy
) {}
