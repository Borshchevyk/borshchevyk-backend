package ru.kubsu.borshchevyk.sync.domain.model;

/**
 * Types of synchronization events supported by the system.
 *
 * @author Aleksey Timko
 * @since 2026-03-01
 */
public enum EventType {
    USER_REGISTERED,
    USER_UPDATED,
    MESSAGE_CREATED,
    MESSAGE_DELETED
}
