package ru.kubsu.borshchevyk.message.domain.model.message;

/**
 * Enumeration representing the delivery and read status of a message.
 *
 * @author Aleksey Timko
 */
public enum MessageStatus {
    RECEIVED_BY_SERVER,
    RECEIVED_BY_USER,
    READ,
    ERROR
}
