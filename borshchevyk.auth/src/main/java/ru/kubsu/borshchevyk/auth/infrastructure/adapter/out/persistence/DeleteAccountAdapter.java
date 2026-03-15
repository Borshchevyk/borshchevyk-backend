package ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.auth.application.port.out.DeleteAccountPort;
import ru.kubsu.borshchevyk.auth.domain.model.value.AccountId;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.persistence.repository.AccountSpringRepository;

@Component
@RequiredArgsConstructor
public class DeleteAccountAdapter implements DeleteAccountPort {

    private final AccountSpringRepository accountRepository;

    @Override
    public void deleteAccount(AccountId accountId) {
        accountRepository.deleteById(accountId.value());
    }
}
