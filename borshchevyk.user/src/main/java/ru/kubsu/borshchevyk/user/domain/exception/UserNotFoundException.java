package ru.kubsu.borshchevyk.user.domain.exception;

public class UserNotFoundException extends UserServiceException {
    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND, "User was not found.");
    }
}
