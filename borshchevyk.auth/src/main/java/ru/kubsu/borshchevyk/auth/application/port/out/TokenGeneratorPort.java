package ru.kubsu.borshchevyk.auth.application.port.out;

import ru.kubsu.borshchevyk.auth.domain.model.account.Account;

/**
 * Port for generating authentication and refresh tokens.
 */
public interface TokenGeneratorPort {
    /**
     * Generates an access token for the given account.
     *
     * @param account the account to generate the token for
     * @return the generated access token
     */
    String generateAccessToken(Account account);

    /**
     * Generates a refresh token for the given account.
     *
     * @param account the account to generate the token for
     * @return the generated refresh token
     */
    String generateRefreshToken(Account account);
}
