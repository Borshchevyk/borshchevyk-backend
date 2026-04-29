package ru.kubsu.borshchevyk.calls.domain.model;

import ru.kubsu.borshchevyk.calls.domain.exception.DomainValidationException;
import java.util.UUID;

/**
 * Value object representing a unique User Identifier.
 *
 * @author Aleksey Timko
 * @since 2026-04-25
 */
public record UserId(UUID value) {
    public UserId {
        if (value == null) {
            throw new DomainValidationException("UserId value cannot be null");
        }
    }
}
