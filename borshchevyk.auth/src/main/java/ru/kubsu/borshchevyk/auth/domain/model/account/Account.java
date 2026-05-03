package ru.kubsu.borshchevyk.auth.domain.model.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.kubsu.borshchevyk.auth.domain.model.value.AccountId;
import ru.kubsu.borshchevyk.auth.domain.model.value.Email;
import ru.kubsu.borshchevyk.auth.domain.model.value.Tag;

/**
 * Domain model representing a user account in the authentication system.
 * Encapsulates the state and business logic of an account.
 */
@Getter
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

    /**
     * Updates the email of the account.
     *
     * @param newEmail the new email to set
     */
    public void updateEmail(Email newEmail) {
        if (newEmail == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        this.email = newEmail;
    }

    /**
     * Updates the tag of the account.
     *
     * @param newTag the new tag to set
     */
    public void updateTag(Tag newTag) {
        if (newTag == null) {
            throw new IllegalArgumentException("Tag cannot be null");
        }
        this.tag = newTag;
    }

    /**
     * Updates the user's password hash.
     *
     * @param newPasswordHash the new password hash
     */
    public void updatePasswordHash(String newPasswordHash) {
        if (newPasswordHash == null || newPasswordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be empty");
        }
        this.passwordHash = newPasswordHash;
    }
}
