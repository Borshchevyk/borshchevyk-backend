package ru.kubsu.borshchevyk.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.auth.application.port.in.SyncAccountUpdatedUseCase;
import ru.kubsu.borshchevyk.auth.application.port.out.DeleteAccountPort;
import ru.kubsu.borshchevyk.auth.application.port.out.LoadAccountByIdPort;
import ru.kubsu.borshchevyk.auth.application.port.out.SaveAccountPort;
import ru.kubsu.borshchevyk.auth.domain.event.UserUpdatedEvent;
import ru.kubsu.borshchevyk.auth.domain.model.account.Account;
import ru.kubsu.borshchevyk.auth.domain.model.value.AccountId;
import ru.kubsu.borshchevyk.auth.domain.model.value.Email;
import ru.kubsu.borshchevyk.auth.domain.model.value.Tag;

import java.util.Optional;

/**
 * Service for synchronizing account data based on user events.
 */
@Service
@RequiredArgsConstructor
public class SyncAccountUpdatedService implements SyncAccountUpdatedUseCase {

    private final LoadAccountByIdPort loadAccountByIdPort;
    private final SaveAccountPort saveAccountPort;

    @Override
    public void syncUpdated(UserUpdatedEvent event) {
        Optional<Account> accountOpt = loadAccountByIdPort.loadAccountById(new AccountId(event.userId()));
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            account.updateEmail(new Email(event.email()));
            account.updateTag(new Tag(event.tag()));
            saveAccountPort.saveAccount(account);
        }
    }
}
