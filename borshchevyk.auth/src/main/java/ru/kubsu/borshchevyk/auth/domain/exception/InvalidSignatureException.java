package ru.kubsu.borshchevyk.auth.domain.exception;

/**
 * Exception thrown when a cryptographic signature is invalid.
 */
public class InvalidSignatureException extends AuthServiceException {
    /**
     * Constructs a new InvalidSignatureException with a default error message.
     */
    public InvalidSignatureException() {
        super(ErrorCode.INVALID_SIGNATURE);
    }
}
