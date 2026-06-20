package ru.kubsu.borshchevyk.media.domain.exception;

/**
 * Exception thrown when a user attempts an action they are not permitted to perform on an attachment.
 *
 * @author Aleksey Timko
 */
public class ForbiddenActionException extends RuntimeException {
    public ForbiddenActionException(String message) {
        super(message);
    }
}
