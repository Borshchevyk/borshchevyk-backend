package ru.kubsu.borshchevyk.auth.domain.exception;

import lombok.Getter;

@Getter
public abstract class AuthServiceException extends RuntimeException {
    private final ErrorCode code;

    public AuthServiceException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public enum ErrorCode {
        USER_ALREADY_EXISTS,
        INVALID_CREDENTIALS,
        INCORRECT_FORMAT
    }
}
