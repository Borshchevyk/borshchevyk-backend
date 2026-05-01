package ru.kubsu.borshchevyk.auth.application.port.out;

import ru.kubsu.borshchevyk.auth.domain.model.value.AccountId;

/**
 * Port for creating saved messages chat for a newly registered user.
 *
 * @author Aleksey Timko
 */
public interface CreateSavedMessagesPort {
    /**
     * Triggers the creation of the saved messages chat for the given account.
     *
     * @param accountId the account ID
     */
    void createSavedMessages(AccountId accountId);
}
