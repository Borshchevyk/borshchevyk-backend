package ru.kubsu.borshchevyk.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.auth.application.dto.command.VerifyCommand;
import ru.kubsu.borshchevyk.auth.application.port.in.VerifyUseCase;
import ru.kubsu.borshchevyk.auth.application.port.out.ChallengeStorePort;
import ru.kubsu.borshchevyk.auth.application.port.out.LoadAccountByIdPort;
import ru.kubsu.borshchevyk.auth.application.port.out.SignatureVerifierPort;
import ru.kubsu.borshchevyk.auth.application.port.out.TokenGeneratorPort;
import ru.kubsu.borshchevyk.auth.domain.exception.AccountNotFoundException;
import ru.kubsu.borshchevyk.auth.domain.exception.ChallengeExpiredException;
import ru.kubsu.borshchevyk.auth.domain.model.account.Account;
import ru.kubsu.borshchevyk.auth.domain.model.result.VerifyResult;
import ru.kubsu.borshchevyk.auth.domain.model.value.AccountId;

/**
 * Service for verifying crypto-signature and issuing tokens.
 */
@Service
@RequiredArgsConstructor
public class VerifyService implements VerifyUseCase {

    private final LoadAccountByIdPort loadAccountByIdPort;
    private final ChallengeStorePort challengeStorePort;
    private final SignatureVerifierPort signatureVerifierPort;
    private final TokenGeneratorPort tokenGeneratorPort;

    @Override
    public VerifyResult verify(VerifyCommand command) {
        AccountId accountId = new AccountId(command.userId());

        Account account = loadAccountByIdPort.loadAccountById(accountId)
                .orElseThrow(AccountNotFoundException::new);

        String challenge = challengeStorePort.getChallenge(accountId)
                .orElseThrow(ChallengeExpiredException::new);

        signatureVerifierPort.verifySignature(challenge, command.signature(), account.getPublicKey());

        challengeStorePort.deleteChallenge(accountId);

        return VerifyResult.builder()
                .accessToken(tokenGeneratorPort.generateAccessToken(account))
                .refreshToken(tokenGeneratorPort.generateRefreshToken(account))
                .build();
    }
}
