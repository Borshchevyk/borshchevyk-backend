package ru.kubsu.borshchevyk.user.domain.exception;

import lombok.Getter;

@Getter
public abstract class UserServiceException extends RuntimeException {
    private final ErrorCode code;

    public UserServiceException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public enum ErrorCode {
        USER_NOT_FOUND,
        USER_ALREADY_EXISTS,
        INCORRECT_FORMAT,
        FORBIDDEN
    }
}
