package ru.kubsu.borshchevyk.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.auth.application.port.in.SyncAccountUseCase;
import ru.kubsu.borshchevyk.auth.application.port.out.DeleteAccountPort;
import ru.kubsu.borshchevyk.auth.application.port.out.LoadAccountPort;
import ru.kubsu.borshchevyk.auth.application.port.out.SaveAccountPort;
import ru.kubsu.borshchevyk.auth.domain.event.UserDeletedEvent;
import ru.kubsu.borshchevyk.auth.domain.event.UserUpdatedEvent;
import ru.kubsu.borshchevyk.auth.domain.model.account.Account;
import ru.kubsu.borshchevyk.auth.domain.model.value.AccountId;
import ru.kubsu.borshchevyk.auth.domain.model.value.Email;
import ru.kubsu.borshchevyk.auth.domain.model.value.Tag;

import java.util.Optional;

/**
 * Service for synchronizing account data based on user events.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Service
@RequiredArgsConstructor
public class SyncAccountService implements SyncAccountUseCase {

    private final LoadAccountPort loadAccountPort;
    private final SaveAccountPort saveAccountPort;
    private final DeleteAccountPort deleteAccountPort;

    @Override
    public void syncUpdated(UserUpdatedEvent event) {
        Optional<Account> accountOpt = loadAccountPort.loadAccount(new AccountId(event.userId()));
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            if (event.email() != null && !event.email().isBlank()) {
                account.updateEmail(new Email(event.email()));
            }
            if (event.tag() != null && !event.tag().isBlank()) {
                account.updateTag(new Tag(event.tag()));
            }
            saveAccountPort.saveAccount(account);
        }
    }

    @Override
    public void syncDeleted(UserDeletedEvent event) {
        deleteAccountPort.delete(new AccountId(event.userId()));
    }
}
