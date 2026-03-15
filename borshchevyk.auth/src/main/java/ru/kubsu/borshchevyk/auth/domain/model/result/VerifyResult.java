package ru.kubsu.borshchevyk.auth.domain.model.result;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * Result of a successful verification or login process, containing JWT tokens.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 * @param accessToken the JWT used for authentication
 * @param refreshToken the JWT used to refresh the session
 */
@Slf4j
@Builder
public record VerifyResult(
        String accessToken,
        String refreshToken
) {
    /**
     * Compact constructor for {@code VerifyResult}.
     * Tokens are not logged for security reasons.
     */
    public VerifyResult {
        log.debug("VerifyResult created with tokens.");
    }
}
