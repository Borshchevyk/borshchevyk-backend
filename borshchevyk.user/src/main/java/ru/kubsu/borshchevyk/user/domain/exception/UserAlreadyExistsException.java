package ru.kubsu.borshchevyk.user.domain.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * Exception thrown when attempting to register a user that already exists in the system.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@Slf4j
public class UserAlreadyExistsException extends UserServiceException {

    /**
     * Constructs a new UserAlreadyExistsException with the specified message.
     *
     * @param message detail message about which user already exists
     */
    public UserAlreadyExistsException(String message) {
        super(ErrorCode.USER_ALREADY_EXISTS, message);
    }
}
