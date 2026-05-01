package ru.kubsu.borshchevyk.message.domain.exception;

/**
 * Base domain exception for the Message service.
 * All specific domain exceptions should inherit from this class to allow uniform exception handling.
 *
 * @author Aleksey Timko
 */
public abstract class MessageDomainException extends RuntimeException {
    public MessageDomainException(String message) {
        super(message);
    }

    public MessageDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
