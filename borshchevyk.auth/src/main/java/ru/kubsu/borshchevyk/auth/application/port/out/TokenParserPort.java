package ru.kubsu.borshchevyk.auth.application.port.out;

import ru.kubsu.borshchevyk.auth.domain.model.value.AccountId;

/**
 * Port for parsing authentication and refresh tokens.
 */
public interface TokenParserPort {
    /**
     * Parses a refresh token and returns the associated account ID.
     *
     * @param token the refresh token to parse
     * @return the account ID extracted from the token
     */
    AccountId parseRefreshToken(String token);
}
