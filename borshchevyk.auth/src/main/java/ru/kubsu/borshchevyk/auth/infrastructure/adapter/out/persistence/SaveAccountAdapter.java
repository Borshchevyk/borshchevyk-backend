package ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.auth.application.port.out.SaveAccountPort;
import ru.kubsu.borshchevyk.auth.domain.model.account.Account;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.persistence.entity.AccountJpaEntity;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.persistence.mapper.AccountPersistenceMapper;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.persistence.repository.AccountSpringRepository;

@Component
@RequiredArgsConstructor
public class SaveAccountAdapter implements SaveAccountPort {

    private final AccountSpringRepository accountRepository;
    private final AccountPersistenceMapper accountMapper;

    @Override
    public void saveAccount(Account account) {
        AccountJpaEntity entity = accountMapper.toEntity(account);
        accountRepository.save(entity);
    }
}
