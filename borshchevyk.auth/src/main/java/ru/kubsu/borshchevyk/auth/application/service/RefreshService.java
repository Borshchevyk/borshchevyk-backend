package ru.kubsu.borshchevyk.auth.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.auth.application.dto.command.RefreshCommand;
import ru.kubsu.borshchevyk.auth.application.port.in.RefreshUseCase;
import ru.kubsu.borshchevyk.auth.application.port.out.LoadAccountPort;
import ru.kubsu.borshchevyk.auth.application.port.out.TokenGeneratorPort;
import ru.kubsu.borshchevyk.auth.application.port.out.TokenParserPort;
import ru.kubsu.borshchevyk.auth.domain.exception.InvalidCredentialsException;
import ru.kubsu.borshchevyk.auth.domain.model.account.Account;
import ru.kubsu.borshchevyk.auth.domain.model.result.VerifyResult;
import ru.kubsu.borshchevyk.auth.domain.model.value.AccountId;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshService implements RefreshUseCase {

    private final TokenParserPort tokenParserPort;
    private final LoadAccountPort loadAccountPort;
    private final TokenGeneratorPort tokenGeneratorPort;

    @Override
    public VerifyResult refresh(RefreshCommand command) {
        log.info("Processing refresh token");
        try {
            AccountId accountId = tokenParserPort.parseRefreshToken(command.refreshToken());
            Account account = loadAccountPort.loadAccount(accountId)
                    .orElseThrow(() -> new InvalidCredentialsException());

            String newAccessToken = tokenGeneratorPort.generateAccessToken(account);
            String newRefreshToken = tokenGeneratorPort.generateRefreshToken(account);

            return new VerifyResult(newAccessToken, newRefreshToken);
        } catch (InvalidCredentialsException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error refreshing token", e);
            throw new InvalidCredentialsException();
        }
    }
}
