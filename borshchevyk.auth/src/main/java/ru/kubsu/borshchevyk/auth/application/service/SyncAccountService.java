package ru.kubsu.borshchevyk.auth.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
@RequiredArgsConstructor
public class SyncAccountService implements SyncAccountUseCase {

    private final LoadAccountPort loadAccountPort;
    private final SaveAccountPort saveAccountPort;
    private final DeleteAccountPort deleteAccountPort;

    /**
     * Syncs account details when a user updated event is received.
     *
     * @param event the user updated event
     */
    @Override
    public void syncUpdated(UserUpdatedEvent event) {
        log.info("Processing sync update for user: {}", event.userId());
        Optional<Account> accountOpt = loadAccountPort.loadAccount(new AccountId(event.userId()));
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            if (event.email() != null && !event.email().isBlank()) {
                account.setEmail(new Email(event.email()));
            }
            if (event.tag() != null && !event.tag().isBlank()) {
                account.setTag(new Tag(event.tag()));
            }
            saveAccountPort.saveAccount(account);
            log.info("Successfully synced updated account for user: {}", event.userId());
        } else {
            log.warn("Account not found for sync update: {}", event.userId());
        }
    }

    /**
     * Deletes account when a user deleted event is received.
     *
     * @param event the user deleted event
     */
    @Override
    public void syncDeleted(UserDeletedEvent event) {
        log.info("Processing sync delete for user: {}", event.userId());
        deleteAccountPort.delete(new AccountId(event.userId()));
        log.info("Successfully synced deleted account for user: {}", event.userId());
    }
}
