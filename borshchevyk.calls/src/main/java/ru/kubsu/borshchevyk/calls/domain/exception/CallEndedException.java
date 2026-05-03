package ru.kubsu.borshchevyk.calls.domain.exception;

/**
 * Domain exception thrown when an operation is attempted on an already ended call.
 */
public class CallEndedException extends RuntimeException {
    public CallEndedException(String message) {
        super(message);
    }
}
