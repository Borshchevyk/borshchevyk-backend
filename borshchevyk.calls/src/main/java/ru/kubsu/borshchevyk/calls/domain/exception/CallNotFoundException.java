package ru.kubsu.borshchevyk.calls.domain.exception;

/**
 * Thrown when a call is not found.
 *
 * @author Gemini
 * @since 2026-04-25
 */
public class CallNotFoundException extends RuntimeException {
    public CallNotFoundException(String message) {
        super(message);
    }
}
