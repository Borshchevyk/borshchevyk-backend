package ru.kubsu.borshchevyk.user.domain.exception;

public class UserAlreadyExistsException extends UserServiceException {
    public UserAlreadyExistsException(String message) {
        super(ErrorCode.USER_ALREADY_EXISTS, message);
    }
}
