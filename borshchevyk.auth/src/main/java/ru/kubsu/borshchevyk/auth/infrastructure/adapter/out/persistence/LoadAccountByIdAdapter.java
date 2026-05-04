package ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.auth.application.port.out.LoadAccountByIdPort;
import ru.kubsu.borshchevyk.auth.domain.model.account.Account;
import ru.kubsu.borshchevyk.auth.domain.model.value.AccountId;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.persistence.mapper.AccountPersistenceMapper;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.persistence.repository.AccountSpringRepository;

import java.util.Optional;

/**
 * Adapter for loading accounts by ID from the database.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoadAccountByIdAdapter implements LoadAccountByIdPort {

    private final AccountSpringRepository accountRepository;
    private final AccountPersistenceMapper accountMapper;

    @Override
    public Optional<Account> loadAccountById(AccountId accountId) {
        log.info("Loading account with ID: {}", accountId.value());
        return accountRepository.findById(accountId.value()).map(accountMapper::toDomain);
    }
}
