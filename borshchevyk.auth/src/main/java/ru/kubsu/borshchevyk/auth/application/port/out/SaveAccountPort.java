package ru.kubsu.borshchevyk.auth.application.port.out;

import ru.kubsu.borshchevyk.auth.domain.model.account.Account;

/**
 * Port for saving an account.
 *
 * @author Aleksey Timko
 */
public interface SaveAccountPort {
    /**
     * Saves the given account.
     *
     * @param account the account to save
     */
    void saveAccount(Account account);
}
