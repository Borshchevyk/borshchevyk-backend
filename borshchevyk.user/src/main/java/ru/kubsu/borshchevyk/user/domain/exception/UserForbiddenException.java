package ru.kubsu.borshchevyk.user.domain.exception;

public class UserForbiddenException extends UserServiceException {

    public UserForbiddenException(String message) {
        super(ErrorCode.FORBIDDEN, message);
    }
}
