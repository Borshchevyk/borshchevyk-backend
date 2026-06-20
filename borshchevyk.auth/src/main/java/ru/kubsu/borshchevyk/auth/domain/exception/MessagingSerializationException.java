package ru.kubsu.borshchevyk.auth.domain.exception;

/**
 * Exception thrown when messaging serialization or deserialization fails.
 */
public class MessagingSerializationException extends AuthServiceException {
    /**
     * Constructs a new MessagingSerializationException with a provided error message.
     */
    public MessagingSerializationException(String message) {
        super(ErrorCode.SERVER_ERROR, message);
    }
}
