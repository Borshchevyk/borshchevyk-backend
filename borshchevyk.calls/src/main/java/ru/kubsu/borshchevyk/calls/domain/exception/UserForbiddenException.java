package ru.kubsu.borshchevyk.calls.domain.exception;

/**
 * Thrown when a user attempts an operation they are not authorized for.
 */
public class UserForbiddenException extends RuntimeException {
    public UserForbiddenException(String message) {
        super(message);
    }
}
