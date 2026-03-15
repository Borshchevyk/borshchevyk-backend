package ru.kubsu.borshchevyk.auth.domain.model.result;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * Result of a registration request.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 * @param userId the unique identifier assigned to the new user
 */
@Slf4j
@Builder
public record RegisterResult(UUID userId) {
    /**
     * Compact constructor for {@code RegisterResult}.
     */
    public RegisterResult {
        log.info("New user registered successfully: {}", userId);
    }
}
