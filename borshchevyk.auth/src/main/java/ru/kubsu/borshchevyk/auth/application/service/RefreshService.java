package ru.kubsu.borshchevyk.auth.application.service;

import lombok.RequiredArgsConstructor;
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

/**
 * Service for refreshing authentication tokens.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Service
@RequiredArgsConstructor
public class RefreshService implements RefreshUseCase {

    private final TokenParserPort tokenParserPort;
    private final LoadAccountPort loadAccountPort;
    private final TokenGeneratorPort tokenGeneratorPort;

    @Override
    public VerifyResult refresh(RefreshCommand command) {
        try {
            AccountId accountId = tokenParserPort.parseRefreshToken(command.refreshToken());
            Account account = loadAccountPort.loadAccount(accountId)
                    .orElseThrow(InvalidCredentialsException::new);

            return VerifyResult.builder()
                    .accessToken(tokenGeneratorPort.generateAccessToken(account))
                    .refreshToken(tokenGeneratorPort.generateRefreshToken(account))
                    .build();
        } catch (InvalidCredentialsException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidCredentialsException();
        }
    }
}
