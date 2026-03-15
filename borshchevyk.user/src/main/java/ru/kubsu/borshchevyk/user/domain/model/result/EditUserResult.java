package ru.kubsu.borshchevyk.user.domain.model.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Result object for user edit operations.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@Slf4j
@Getter
@Builder
@AllArgsConstructor
public class EditUserResult {
    /**
     * Unique identifier of the edited user.
     */
    private final String userId;

    /**
     * Updated email address.
     */
    private final String email;

    /**
     * Updated user tag.
     */
    private final String tag;
}
