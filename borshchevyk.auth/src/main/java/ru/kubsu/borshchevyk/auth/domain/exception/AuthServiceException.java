package ru.kubsu.borshchevyk.auth.domain.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.Instant;

/**
 * Base exception for all authentication-related service errors.
 */
@Getter
public abstract class AuthServiceException extends RuntimeException {

    private final ErrorCode code;
    private final Object[] args;
    private final Instant instant = Instant.now();
    private final HttpStatus httpStatus;

    public AuthServiceException(ErrorCode code, Object... args) {
        super(code.name());
        this.code = code;
        this.httpStatus = code.httpStatus;
        this.args = args;
    }

    public static AuthServiceException getDefaultException() {
        return new AuthServiceException(
                ErrorCode.SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR
        ) {};
    }

    public record ErrorResponse(
            AuthServiceException.ErrorCode code,
            String message,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            Instant timestamp
    ) { }

    /**
     * Supported business error codes.
     */
    public enum ErrorCode {
        /**
         * User with the given email already exists in the system.
         */
        USER_ALREADY_EXISTS(HttpStatus.CONFLICT),
        /**
         * Provided login credentials (email or password) are incorrect.
         */
        INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED),
        /**
         * Input format for email or tag is invalid.
         */
        INCORRECT_FORMAT(HttpStatus.BAD_REQUEST),
        /**
         * Challenge has expired or was not found.
         */
        CHALLENGE_EXPIRED(HttpStatus.UNAUTHORIZED),
        /**
         * Cryptographic signature is invalid.
         */
        INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED),
        /**
         * Account not found.
         */
        ACCOUNT_NOT_FOUND(HttpStatus.UNAUTHORIZED),
        /**
         * Any server error.
         */
        SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);

        private final HttpStatus httpStatus;
        private static final String ERROR_PREFIX = "error.";

        ErrorCode(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
        }

        @Override
        public String toString() {
            return ERROR_PREFIX + this.name().toUpperCase();
        }
    }
}
