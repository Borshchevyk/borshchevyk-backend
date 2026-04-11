package ru.kubsu.borshchevyk.auth.domain.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * Exception thrown when an account is not found.
 */
@Slf4j
public class AccountNotFoundException extends AuthServiceException {
    /**
     * Constructs a new AccountNotFoundException with a default error message.
     */
    public AccountNotFoundException() {
        super(ErrorCode.ACCOUNT_NOT_FOUND, "Account not found.");
    }
}
