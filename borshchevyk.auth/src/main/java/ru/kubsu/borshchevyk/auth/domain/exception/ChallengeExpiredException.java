package ru.kubsu.borshchevyk.auth.domain.exception;

/**
 * Exception thrown when a challenge has expired or was not found.
 */
public class ChallengeExpiredException extends AuthServiceException {
    /**
     * Constructs a new ChallengeExpiredException with a default error message.
     */
    public ChallengeExpiredException() {
        super(ErrorCode.CHALLENGE_EXPIRED);
    }
}
