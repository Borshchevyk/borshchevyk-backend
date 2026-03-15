package ru.kubsu.borshchevyk.auth.domain.exception;

public class InvalidCredentialsException extends AuthServiceException {
    public InvalidCredentialsException() {
        super(ErrorCode.INVALID_CREDENTIALS, "Incorrect email or password.");
    }
}
