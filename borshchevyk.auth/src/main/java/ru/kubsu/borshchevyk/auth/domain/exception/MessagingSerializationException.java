package ru.kubsu.borshchevyk.auth.domain.exception;

/**
 * Exception thrown when messaging serialization or deserialization fails.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
public class MessagingSerializationException extends RuntimeException {

    public MessagingSerializationException(String message) {
        super(message);
    }

    public MessagingSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
