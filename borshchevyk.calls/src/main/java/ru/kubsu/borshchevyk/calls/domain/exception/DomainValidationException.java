package ru.kubsu.borshchevyk.calls.domain.exception;

/**
 * Exception thrown when domain validation rules are violated.
 *
 * @author Aleksey Timko
 * @since 2026-04-25
 */
public class DomainValidationException extends RuntimeException {
    public DomainValidationException(String message) {
        super(message);
    }
}
