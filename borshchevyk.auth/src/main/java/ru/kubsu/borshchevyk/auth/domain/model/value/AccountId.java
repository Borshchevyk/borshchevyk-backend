package ru.kubsu.borshchevyk.auth.domain.model.value;

import lombok.extern.slf4j.Slf4j;
import java.util.UUID;

/**
 * Value object representing an account unique identifier.
 *
 * @param value the UUID value
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Slf4j
public record AccountId(UUID value) {
    /**
     * Constructs a new AccountId.
     *
     * @param value the UUID value
     */
    public AccountId {
        if (value == null) {
            log.warn("Attempted to create AccountId with null value");
        }
    }
}
