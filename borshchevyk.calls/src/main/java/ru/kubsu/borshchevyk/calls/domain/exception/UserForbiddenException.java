package ru.kubsu.borshchevyk.calls.domain.exception;

public class UserForbiddenException extends CallServiceException {
    public UserForbiddenException() {
        super(ErrorCode.USER_FORBIDDEN);
    }
}
