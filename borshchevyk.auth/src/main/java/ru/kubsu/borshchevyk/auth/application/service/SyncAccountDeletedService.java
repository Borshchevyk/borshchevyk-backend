package ru.kubsu.borshchevyk.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.auth.application.port.in.SyncAccountDeletedUseCase;
import ru.kubsu.borshchevyk.auth.application.port.out.DeleteAccountPort;
import ru.kubsu.borshchevyk.auth.application.port.out.LoadAccountByIdPort;
import ru.kubsu.borshchevyk.auth.application.port.out.SaveAccountPort;
import ru.kubsu.borshchevyk.auth.domain.event.UserDeletedEvent;
import ru.kubsu.borshchevyk.auth.domain.model.value.AccountId;

/**
 * Service for synchronizing account data based on user events.
 */
@Service
@RequiredArgsConstructor
public class SyncAccountDeletedService implements SyncAccountDeletedUseCase {

    private final DeleteAccountPort deleteAccountPort;

    @Override
    public void syncDeleted(UserDeletedEvent event) {
        deleteAccountPort.delete(new AccountId(event.userId()));
    }
}
