package ru.kubsu.borshchevyk.auth.application.port.out;

import ru.kubsu.borshchevyk.auth.domain.model.value.AccountId;

/**
 * Port for deleting an account.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
public interface DeleteAccountPort {
    /**
     * Deletes an account by its unique identifier.
     *
     * @param accountId the account ID to delete
     */
    void delete(AccountId accountId);
}
