package ru.kubsu.borshchevyk.auth.application.port.out;

import ru.kubsu.borshchevyk.auth.domain.model.value.AccountId;

public interface DeleteAccountPort {
    void deleteAccount(AccountId accountId);
}
