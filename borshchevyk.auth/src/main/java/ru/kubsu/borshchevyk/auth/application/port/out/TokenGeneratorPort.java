package ru.kubsu.borshchevyk.auth.application.port.out;

import ru.kubsu.borshchevyk.auth.domain.model.account.Account;

public interface TokenGeneratorPort {
    String generateAccessToken(Account account);
    String generateRefreshToken(Account account);
}
