package ru.kubsu.borshchevyk.message.domain.exception;

/**
 * Exception thrown when messaging serialization or deserialization fails within the domain context.
 *
 * @author Aleksey Timko
 */
public class MessagingSerializationException extends MessageDomainException {

    public MessagingSerializationException(String message) {
        super(message);
    }

    public MessagingSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
