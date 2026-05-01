package ru.kubsu.borshchevyk.message.domain.exception;

/**
 * Exception thrown when a requested chat cannot be found in the domain.
 *
 * @author Aleksey Timko
 */
public class ChatNotFoundException extends MessageDomainException {
    public ChatNotFoundException(String message) {
        super(message);
    }
}
