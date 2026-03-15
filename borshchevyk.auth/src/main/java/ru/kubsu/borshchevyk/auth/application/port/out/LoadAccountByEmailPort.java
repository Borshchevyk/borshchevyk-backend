package ru.kubsu.borshchevyk.auth.application.port.out;

import ru.kubsu.borshchevyk.auth.domain.model.account.Account;
import ru.kubsu.borshchevyk.auth.domain.model.value.Email;

import java.util.Optional;

public interface LoadAccountByEmailPort {
    Optional<Account> loadAccountByEmail(Email email);
}
