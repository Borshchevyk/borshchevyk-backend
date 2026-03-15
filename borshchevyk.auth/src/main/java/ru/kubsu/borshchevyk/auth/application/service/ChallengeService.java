package ru.kubsu.borshchevyk.auth.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.auth.application.dto.command.ChallengeCommand;
import ru.kubsu.borshchevyk.auth.application.port.in.ChallengeUseCase;
import ru.kubsu.borshchevyk.auth.application.port.out.ChallengeStorePort;
import ru.kubsu.borshchevyk.auth.application.port.out.LoadAccountPort;
import ru.kubsu.borshchevyk.auth.domain.exception.InvalidCredentialsException;
import ru.kubsu.borshchevyk.auth.domain.model.account.Account;
import ru.kubsu.borshchevyk.auth.domain.model.result.ChallengeResult;
import ru.kubsu.borshchevyk.auth.domain.model.value.AccountId;

import java.util.UUID;

/**
 * Service for generating auth challenges for crypto-signature verification.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeService implements ChallengeUseCase {

    private final LoadAccountPort loadAccountPort;
    private final ChallengeStorePort challengeStorePort;

    /**
     * Generates a unique challenge for the given user.
     *
     * @param command the challenge command containing user ID
     * @return the challenge result containing the generated challenge string
     * @throws InvalidCredentialsException if the account is not found
     */
    @Override
    public ChallengeResult challenge(ChallengeCommand command) {
        log.info("Generating challenge for user: {}", command.userId());
        AccountId accountId = new AccountId(command.userId());
        
        loadAccountPort.loadAccount(accountId)
                .orElseThrow(() -> {
                    log.warn("Challenge generation failed: account not found for user {}", command.userId());
                    return new InvalidCredentialsException();
                });

        String generatedChallenge = UUID.randomUUID().toString();
        
        challengeStorePort.saveChallenge(accountId, generatedChallenge);
        
        log.info("Successfully generated challenge for user: {}", command.userId());
        return ChallengeResult.builder()
                .challenge(generatedChallenge)
                .build();
    }
}
