package ru.kubsu.borshchevyk.calls.domain.model;

import ru.kubsu.borshchevyk.calls.domain.exception.DomainValidationException;
import java.util.UUID;

/**
 * Value object representing a unique Call Identifier.
 */
public record CallId(UUID value) {
    public CallId {
        if (value == null) {
            throw new DomainValidationException("CallId value cannot be null");
        }
    }

    public static CallId generate() {
        return new CallId(UUID.randomUUID());
    }
}
