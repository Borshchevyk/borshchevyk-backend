package ru.kubsu.borshchevyk.message.domain.exception;

/**
 * Exception thrown when messaging serialization or deserialization fails.
 */
public class MessagingSerializationException extends RuntimeException {

    public MessagingSerializationException(String message) {
        super(message);
    }

    public MessagingSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
