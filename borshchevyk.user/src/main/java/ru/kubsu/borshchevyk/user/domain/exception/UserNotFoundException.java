package ru.kubsu.borshchevyk.user.domain.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * Exception thrown when a requested user cannot be found in the persistent store.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@Slf4j
public class UserNotFoundException extends UserServiceException {

    /**
     * Constructs a new UserNotFoundException with a default "User was not found." message.
     */
    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND, "User was not found.");
    }
}
