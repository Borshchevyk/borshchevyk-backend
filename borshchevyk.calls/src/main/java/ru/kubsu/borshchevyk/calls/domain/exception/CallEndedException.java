package ru.kubsu.borshchevyk.calls.domain.exception;

/**
 * Domain exception thrown when an operation is attempted on an already ended call.
 *
 * @author Aleksey Timko
 * @since 2026-04-25
 */
public class CallEndedException extends RuntimeException {
    public CallEndedException(String message) {
        super(message);
    }
}
