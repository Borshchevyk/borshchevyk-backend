package ru.kubsu.borshchevyk.auth.domain.model.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Result of a challenge generation request.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Slf4j
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeResult {
    /**
     * The generated challenge string for the user to sign.
     */
    private String challenge;
}
