package ru.kubsu.borshchevyk.auth.domain.model.value;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing an account unique identifier.
 *
 * @param value the UUID value
 */
public record AccountId(UUID value) {
    /**
     * Constructs a new AccountId.
     *
     * @param value the UUID value
     * @throws NullPointerException if value is null
     */
    public AccountId {
        Objects.requireNonNull(value, "AccountId value must not be null");
    }
}