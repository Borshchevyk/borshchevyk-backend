package ru.kubsu.borshchevyk.auth.application.port.out;

import ru.kubsu.borshchevyk.auth.domain.model.value.AccountId;

import java.util.Optional;

public interface ChallengeStorePort {
    void saveChallenge(AccountId accountId, String challenge);
    Optional<String> getChallenge(AccountId accountId);
    void deleteChallenge(AccountId accountId);
}
