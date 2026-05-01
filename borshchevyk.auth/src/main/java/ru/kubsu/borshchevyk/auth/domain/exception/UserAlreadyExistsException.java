package ru.kubsu.borshchevyk.auth.domain.exception;

/**
 * Exception thrown when attempting to register a user with an already existing email.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
public class UserAlreadyExistsException extends AuthServiceException {
    /**
     * Constructs the exception with the email that already exists.
     *
     * @param email the email that is already registered in the system
     */
    public UserAlreadyExistsException(String email) {
        super(ErrorCode.USER_ALREADY_EXISTS, "User with the provided email already exists.");
    }
}
