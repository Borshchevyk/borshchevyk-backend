package ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.auth.application.port.out.LoadAccountByEmailPort;
import ru.kubsu.borshchevyk.auth.domain.model.account.Account;
import ru.kubsu.borshchevyk.auth.domain.model.value.Email;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.persistence.mapper.AccountPersistenceMapper;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.persistence.repository.AccountSpringRepository;

import java.util.Optional;

/**
 * Adapter for loading accounts by email from the database.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoadAccountByEmailAdapter implements LoadAccountByEmailPort {

    private final AccountSpringRepository accountRepository;
    private final AccountPersistenceMapper accountMapper;

    @Override
    public Optional<Account> loadAccountByEmail(Email email) {
        log.info("Loading account by email: {}", email.getValue());
        return accountRepository.findByEmail(email.getValue())
                .map(accountMapper::toDomain);
    }
}
