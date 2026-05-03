package ru.kubsu.borshchevyk.calls.domain.exception;

/**
 * Thrown when a call is not found.
 */
public class CallNotFoundException extends RuntimeException {
    public CallNotFoundException(String message) {
        super(message);
    }
}
