package ru.kubsu.borshchevyk.calls.domain.exception;

/**
 * Thrown when a user attempts an operation they are not authorized for.
 *
 * @author Aleksey Timko
 * @since 2026-04-25
 */
public class UserForbiddenException extends RuntimeException {
    public UserForbiddenException(String message) {
        super(message);
    }
}
