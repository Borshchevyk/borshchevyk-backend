package ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.auth.application.port.out.SaveAccountPort;
import ru.kubsu.borshchevyk.auth.domain.model.account.Account;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.persistence.entity.AccountJpaEntity;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.persistence.mapper.AccountPersistenceMapper;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.persistence.repository.AccountSpringRepository;

/**
 * Adapter for saving accounts to the database.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SaveAccountAdapter implements SaveAccountPort {

    private final AccountSpringRepository accountRepository;
    private final AccountPersistenceMapper accountMapper;

    @Override
    public void saveAccount(Account account) {
        log.info("Saving account");
        AccountJpaEntity entity = accountMapper.toEntity(account);
        accountRepository.save(entity);
        log.info("Account saved successfully: {}", entity.getId());
    }
}
