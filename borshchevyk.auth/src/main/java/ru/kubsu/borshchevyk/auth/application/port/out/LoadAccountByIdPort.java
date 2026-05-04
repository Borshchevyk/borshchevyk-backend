package ru.kubsu.borshchevyk.auth.application.port.out;

import ru.kubsu.borshchevyk.auth.domain.model.account.Account;
import ru.kubsu.borshchevyk.auth.domain.model.value.AccountId;

import java.util.Optional;

/**
 * Port for loading an account by its unique identifier.
 */
public interface LoadAccountByIdPort {
    /**
     * Loads an account by its unique identifier.
     *
     * @param accountId the unique identifier of the account
     * @return an Optional containing the account if found, empty otherwise
     */
    Optional<Account> loadAccountById(AccountId accountId);
}
