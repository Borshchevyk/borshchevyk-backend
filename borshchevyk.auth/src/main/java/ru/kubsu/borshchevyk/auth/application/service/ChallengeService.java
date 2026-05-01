package ru.kubsu.borshchevyk.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.auth.application.dto.command.ChallengeCommand;
import ru.kubsu.borshchevyk.auth.application.port.in.ChallengeUseCase;
import ru.kubsu.borshchevyk.auth.application.port.out.ChallengeStorePort;
import ru.kubsu.borshchevyk.auth.application.port.out.LoadAccountPort;
import ru.kubsu.borshchevyk.auth.domain.exception.InvalidCredentialsException;
import ru.kubsu.borshchevyk.auth.domain.model.result.ChallengeResult;
import ru.kubsu.borshchevyk.auth.domain.model.value.AccountId;

import java.util.UUID;

/**
 * Service for generating auth challenges for crypto-signature verification.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Service
@RequiredArgsConstructor
public class ChallengeService implements ChallengeUseCase {

    private final LoadAccountPort loadAccountPort;
    private final ChallengeStorePort challengeStorePort;

    @Override
    public ChallengeResult challenge(ChallengeCommand command) {
        AccountId accountId = new AccountId(command.userId());
        
        loadAccountPort.loadAccount(accountId)
                .orElseThrow(InvalidCredentialsException::new);

        String generatedChallenge = UUID.randomUUID().toString();
        
        challengeStorePort.saveChallenge(accountId, generatedChallenge);
        
        return ChallengeResult.builder()
                .challenge(generatedChallenge)
                .build();
    }
}
