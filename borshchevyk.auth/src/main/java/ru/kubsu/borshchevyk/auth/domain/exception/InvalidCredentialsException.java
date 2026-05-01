package ru.kubsu.borshchevyk.auth.domain.exception;

/**
 * Exception thrown when authentication fails due to incorrect credentials.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
public class InvalidCredentialsException extends AuthServiceException {
    /**
     * Constructs a new invalid credentials exception with a default error message.
     */
    public InvalidCredentialsException() {
        super(ErrorCode.INVALID_CREDENTIALS, "Incorrect email or password.");
    }
}
