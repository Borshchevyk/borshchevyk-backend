package ru.kubsu.borshchevyk.calls.domain.value;

import java.util.UUID;

/**
 * Value object representing a unique User Identifier.
 */
public record UserId(UUID value) {
    public UserId {
        if (value == null) {
            throw new IllegalArgumentException("UserId value cannot be null");
        }
    }
}
