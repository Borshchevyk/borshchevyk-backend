package ru.kubsu.borshchevyk.message.domain.exception;

/**
 * Exception thrown when a user attempts to perform an action they are not permitted to do within the chat domain.
 *
 * @author Aleksey Timko
 */
public class ForbiddenActionException extends MessageDomainException {
    public ForbiddenActionException(String message) {
        super(message);
    }
}
