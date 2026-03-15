package ru.kubsu.borshchevyk.auth.domain.exception;

public class UserAlreadyExistsException extends AuthServiceException {
    public UserAlreadyExistsException(String email) {
        super(ErrorCode.USER_ALREADY_EXISTS, String.format("User with email %s already exists.", email));
    }
}
