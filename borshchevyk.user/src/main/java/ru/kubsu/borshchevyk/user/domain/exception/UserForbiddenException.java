package ru.kubsu.borshchevyk.user.domain.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * Exception thrown when a user operation is forbidden due to lack of permissions.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@Slf4j
public class UserForbiddenException extends UserServiceException {

    /**
     * Constructs a new UserForbiddenException with the specified detail message.
     *
     * @param message the detail message explaining why the action is forbidden
     */
    public UserForbiddenException(String message) {
        super(ErrorCode.FORBIDDEN, message);
    }
}
