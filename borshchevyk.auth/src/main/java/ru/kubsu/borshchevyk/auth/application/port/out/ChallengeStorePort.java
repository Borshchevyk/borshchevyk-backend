package ru.kubsu.borshchevyk.auth.application.port.out;

import ru.kubsu.borshchevyk.auth.domain.model.value.AccountId;

import java.util.Optional;

/**
 * Port for storing, retrieving, and deleting authentication challenges.
 */
public interface ChallengeStorePort {
    /**
     * Saves a challenge for the given account.
     *
     * @param accountId the account ID
     * @param challenge the challenge string
     */
    void saveChallenge(AccountId accountId, String challenge);

    /**
     * Retrieves the challenge for the given account.
     *
     * @param accountId the account ID
     * @return an Optional containing the challenge if found, empty otherwise
     */
    Optional<String> getChallenge(AccountId accountId);

    /**
     * Deletes the challenge for the given account.
     *
     * @param accountId the account ID
     */
    void deleteChallenge(AccountId accountId);
}
