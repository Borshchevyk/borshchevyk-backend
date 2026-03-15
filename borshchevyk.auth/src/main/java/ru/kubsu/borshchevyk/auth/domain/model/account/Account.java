package ru.kubsu.borshchevyk.auth.domain.model.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.kubsu.borshchevyk.auth.domain.model.value.AccountId;
import ru.kubsu.borshchevyk.auth.domain.model.value.Email;
import ru.kubsu.borshchevyk.auth.domain.model.value.Tag;

@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Account {

    @EqualsAndHashCode.Include
    private final AccountId accountId;

    private Email email;
    private Tag tag;
    private String passwordHash;
    private String publicKey;
    private String encryptedPrivateKey;
}
