package ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.auth.application.port.out.DeleteAccountPort;
import ru.kubsu.borshchevyk.auth.domain.model.value.AccountId;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.persistence.repository.AccountSpringRepository;

/**
 * Adapter for deleting accounts from the database.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteAccountAdapter implements DeleteAccountPort {

    private final AccountSpringRepository accountRepository;

    @Override
    public void delete(AccountId accountId) {
        log.info("Deleting account with ID: {}", accountId.value());
        accountRepository.deleteById(accountId.value());
        log.info("Account deleted successfully: {}", accountId.value());
    }
}
