package ru.kubsu.borshchevyk.user.domain.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Base abstract exception for all user-related business errors.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@Slf4j
@Getter
public abstract class UserServiceException extends RuntimeException {
    /**
     * Specific error code identifying the type of user error.
     */
    private final ErrorCode code;

    /**
     * Constructs a new UserServiceException with the specified error code and message.
     *
     * @param code    the error code
     * @param message the detail message
     */
    public UserServiceException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Enumeration of supported error codes in the User service.
     */
    public enum ErrorCode {
        USER_NOT_FOUND,
        USER_ALREADY_EXISTS,
        INCORRECT_FORMAT,
        FORBIDDEN
    }
}
