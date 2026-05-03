package ru.kubsu.borshchevyk.auth.domain.exception;

import lombok.Getter;

/**
 * Base exception for all authentication-related service errors.
 */
@Getter
public abstract class AuthServiceException extends RuntimeException {
    /**
     * The unique business error code associated with this exception.
     */
    private final ErrorCode code;

    /**
     * Constructs a new authentication service exception.
     *
     * @param code    the business error code
     * @param message the descriptive error message
     */
    public AuthServiceException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Supported business error codes.
     */
    public enum ErrorCode {
        /**
         * User with the given email already exists in the system.
         */
        USER_ALREADY_EXISTS,
        /**
         * Provided login credentials (email or password) are incorrect.
         */
        INVALID_CREDENTIALS,
        /**
         * Input format for email or tag is invalid.
         */
        INCORRECT_FORMAT,
        /**
         * Challenge has expired or was not found.
         */
        CHALLENGE_EXPIRED,
        /**
         * Cryptographic signature is invalid.
         */
        INVALID_SIGNATURE,
        /**
         * Account not found.
         */
        ACCOUNT_NOT_FOUND,
        /**
         * Serialization error.
         */
        SERIALIZATION_ERROR
    }
}
