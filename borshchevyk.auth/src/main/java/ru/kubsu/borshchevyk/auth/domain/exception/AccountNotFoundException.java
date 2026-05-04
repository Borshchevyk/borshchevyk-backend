package ru.kubsu.borshchevyk.auth.domain.exception;

/**
 * Exception thrown when an account is not found.
 */
public class AccountNotFoundException extends AuthServiceException {
    /**
     * Constructs a new AccountNotFoundException with a default error message.
     */
    public AccountNotFoundException() {
        super(ErrorCode.ACCOUNT_NOT_FOUND);
    }
}
