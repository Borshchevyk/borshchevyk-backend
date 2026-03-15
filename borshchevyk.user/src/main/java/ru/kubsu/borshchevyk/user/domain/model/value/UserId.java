package ru.kubsu.borshchevyk.user.domain.model.value;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * Unique identifier for a User domain entity.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@Slf4j
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class UserId {
    /**
     * The UUID value of the user identifier.
     */
    private final UUID value;
}
