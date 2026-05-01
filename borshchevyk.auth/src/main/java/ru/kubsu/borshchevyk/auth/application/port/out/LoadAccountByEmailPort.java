package ru.kubsu.borshchevyk.auth.application.port.out;

import ru.kubsu.borshchevyk.auth.domain.model.account.Account;
import ru.kubsu.borshchevyk.auth.domain.model.value.Email;

import java.util.Optional;

/**
 * Port for loading an account by its email address.
 *
 * @author Aleksey Timko
 */
public interface LoadAccountByEmailPort {
    /**
     * Loads an account by its email address.
     *
     * @param email the email address to search for
     * @return an Optional containing the account if found, empty otherwise
     */
    Optional<Account> loadAccountByEmail(Email email);
}
