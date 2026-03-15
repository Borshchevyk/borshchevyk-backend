package ru.kubsu.borshchevyk.auth.application.port.out;

import ru.kubsu.borshchevyk.auth.domain.model.account.Account;
import ru.kubsu.borshchevyk.auth.domain.model.value.AccountId;

import java.util.Optional;

public interface LoadAccountPort {
    Optional<Account> loadAccount(AccountId accountId);
}
