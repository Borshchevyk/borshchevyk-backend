package ru.kubsu.borshchevyk.auth.domain.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * Exception thrown when a challenge has expired or was not found.
 */
@Slf4j
public class ChallengeExpiredException extends AuthServiceException {
    /**
     * Constructs a new ChallengeExpiredException with a default error message.
     */
    public ChallengeExpiredException() {
        super(ErrorCode.CHALLENGE_EXPIRED, "Challenge expired or not found.");
    }
}
