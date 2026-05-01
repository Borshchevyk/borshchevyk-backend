package ru.kubsu.borshchevyk.message.domain.exception;

/**
 * Exception thrown when a requested message cannot be found in the domain.
 *
 * @author Aleksey Timko
 */
public class MessageNotFoundException extends MessageDomainException {
    public MessageNotFoundException(String message) {
        super(message);
    }
}
