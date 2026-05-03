package ru.kubsu.borshchevyk.calls.domain.exception;

/**
 * Exception thrown when domain validation rules are violated.
 */
public class DomainValidationException extends RuntimeException {
    public DomainValidationException(String message) {
        super(message);
    }
}
