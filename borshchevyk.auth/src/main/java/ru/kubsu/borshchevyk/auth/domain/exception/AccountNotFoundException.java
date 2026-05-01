package ru.kubsu.borshchevyk.auth.domain.exception;

/**
 * Exception thrown when an account is not found.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
public class AccountNotFoundException extends AuthServiceException {
    /**
     * Constructs a new AccountNotFoundException with a default error message.
     */
    public AccountNotFoundException() {
        super(ErrorCode.ACCOUNT_NOT_FOUND, "Account not found.");
    }
}
