package ru.kubsu.borshchevyk.auth.domain.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * Exception thrown when a cryptographic signature is invalid.
 */
@Slf4j
public class InvalidSignatureException extends AuthServiceException {
    /**
     * Constructs a new InvalidSignatureException with a default error message.
     */
    public InvalidSignatureException() {
        super(ErrorCode.INVALID_SIGNATURE, "Invalid cryptographic signature.");
    }
}
