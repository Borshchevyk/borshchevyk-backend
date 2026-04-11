package ru.kubsu.borshchevyk.auth.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.auth.application.dto.command.VerifyCommand;
import ru.kubsu.borshchevyk.auth.application.port.in.VerifyUseCase;
import ru.kubsu.borshchevyk.auth.application.port.out.ChallengeStorePort;
import ru.kubsu.borshchevyk.auth.application.port.out.LoadAccountPort;
import ru.kubsu.borshchevyk.auth.application.port.out.SignatureVerifierPort;
import ru.kubsu.borshchevyk.auth.application.port.out.TokenGeneratorPort;
import ru.kubsu.borshchevyk.auth.domain.exception.AccountNotFoundException;
import ru.kubsu.borshchevyk.auth.domain.exception.ChallengeExpiredException;
import ru.kubsu.borshchevyk.auth.domain.exception.InvalidSignatureException;
import ru.kubsu.borshchevyk.auth.domain.model.account.Account;
import ru.kubsu.borshchevyk.auth.domain.model.result.VerifyResult;
import ru.kubsu.borshchevyk.auth.domain.model.value.AccountId;

/**
 * Service for verifying crypto-signature and issuing tokens.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VerifyService implements VerifyUseCase {

    private final LoadAccountPort loadAccountPort;
    private final ChallengeStorePort challengeStorePort;
    private final SignatureVerifierPort signatureVerifierPort;
    private final TokenGeneratorPort tokenGeneratorPort;

    /**
     * Verifies the provided signature against the stored challenge and issues tokens.
     *
     * @param command the verify command containing user ID and signature
     * @return the verification result containing access and refresh tokens
     * @throws AccountNotFoundException if account not found
     * @throws ChallengeExpiredException if challenge not found
     * @throws InvalidSignatureException if signature is invalid
     */
    @Override
    public VerifyResult verify(VerifyCommand command) {
        log.info("Attempting signature verification for user: {}", command.userId());
        AccountId accountId = new AccountId(command.userId());

        Account account = loadAccountPort.loadAccount(accountId)
                .orElseThrow(() -> {
                    log.warn("Verification failed: account not found for user {}", command.userId());
                    return new AccountNotFoundException();
                });

        String challenge = challengeStorePort.getChallenge(accountId)
                .orElseThrow(() -> {
                    log.warn("Verification failed: challenge not found for user {}", command.userId());
                    return new ChallengeExpiredException();
                });

        boolean isValid = signatureVerifierPort.verifySignature(challenge, command.signature(), account.getPublicKey());
        if (!isValid) {
            log.warn("Verification failed: invalid signature for user {}", command.userId());
            throw new InvalidSignatureException();
        }

        challengeStorePort.deleteChallenge(accountId);

        String accessToken = tokenGeneratorPort.generateAccessToken(account);
        String refreshToken = tokenGeneratorPort.generateRefreshToken(account);

        log.info("Successfully verified signature and issued tokens for user: {}", command.userId());
        return VerifyResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
