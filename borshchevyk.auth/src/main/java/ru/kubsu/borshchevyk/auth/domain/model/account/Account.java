package ru.kubsu.borshchevyk.auth.domain.model.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.kubsu.borshchevyk.auth.domain.model.value.AccountId;
import ru.kubsu.borshchevyk.auth.domain.model.value.Email;
import ru.kubsu.borshchevyk.auth.domain.model.value.Tag;

/**
 * Domain model representing a user account in the authentication system.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Slf4j
@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Account {

    /**
     * Unique identifier for the account.
     */
    @EqualsAndHashCode.Include
    private final AccountId accountId;

    /**
     * Email address associated with the account.
     */
    private Email email;

    /**
     * User tag or display name.
     */
    private Tag tag;

    /**
     * Hashed version of the user's password.
     * <b>Note:</b> This field is sensitive and should never be logged or exposed.
     */
    private String passwordHash;

    /**
     * User's public key for cryptographic operations.
     */
    private String publicKey;

    /**
     * User's private key, encrypted with their password.
     */
    private String encryptedPrivateKey;
}
