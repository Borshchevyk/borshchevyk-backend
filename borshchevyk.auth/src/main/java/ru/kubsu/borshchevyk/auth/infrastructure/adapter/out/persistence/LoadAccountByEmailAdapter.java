package ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.auth.application.port.out.LoadAccountByEmailPort;
import ru.kubsu.borshchevyk.auth.domain.model.account.Account;
import ru.kubsu.borshchevyk.auth.domain.model.value.Email;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.persistence.mapper.AccountPersistenceMapper;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.persistence.repository.AccountSpringRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LoadAccountByEmailAdapter implements LoadAccountByEmailPort {

    private final AccountSpringRepository accountRepository;
    private final AccountPersistenceMapper accountMapper;

    @Override
    public Optional<Account> loadAccountByEmail(Email email) {
        return accountRepository.findByEmail(email.getValue())
                .map(accountMapper::toDomain);
    }
}
