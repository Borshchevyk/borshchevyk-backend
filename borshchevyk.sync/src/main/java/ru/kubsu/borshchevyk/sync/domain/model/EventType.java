package ru.kubsu.borshchevyk.sync.domain.model;

/**
 * Types of synchronization events supported by the system for CRDT/Vector Clocks.
 *
 * @author Aleksey Timko
 */
public enum EventType {
    // User Domain
    USER_REGISTERED,
    USER_UPDATED,
    USER_DELETED,

    // Message Domain
    MESSAGE_CREATED,
    MESSAGE_UPDATED,
    MESSAGE_DELETED,
    
    // Chat Domain
    CHAT_CREATED,
    CHAT_UPDATED,
    CHAT_DELETED,
    MEMBER_ADDED,
    MEMBER_REMOVED,
    MEMBER_UPDATED,

    // Call Domain
    CALL_EVENT,
    
    // Additional Domains
    MESSAGE_READ,
    CONTACT_ADDED,
    CONTACT_REMOVED,
    CONTACT_UPDATED,
    PRIVACY_SETTINGS_UPDATED
}
