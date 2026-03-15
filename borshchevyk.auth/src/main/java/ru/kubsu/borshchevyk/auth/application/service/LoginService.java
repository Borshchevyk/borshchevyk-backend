package ru.kubsu.borshchevyk.auth.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.auth.application.dto.command.LoginCommand;
import ru.kubsu.borshchevyk.auth.application.port.in.LoginUseCase;
import ru.kubsu.borshchevyk.auth.application.port.out.LoadAccountByEmailPort;
import ru.kubsu.borshchevyk.auth.application.port.out.PasswordEncoderPort;
import ru.kubsu.borshchevyk.auth.domain.exception.InvalidCredentialsException;
import ru.kubsu.borshchevyk.auth.domain.model.account.Account;
import ru.kubsu.borshchevyk.auth.domain.model.result.LoginResult;
import ru.kubsu.borshchevyk.auth.domain.model.value.Email;

/**
 * Service for user login authentication.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService implements LoginUseCase {

    private final LoadAccountByEmailPort loadAccountByEmailPort;
    private final PasswordEncoderPort passwordEncoderPort;

    /**
     * Authenticates a user based on email and password hash.
     *
     * @param command the login command containing email and password hash
     * @return the login result containing account details for further authentication
     * @throws InvalidCredentialsException if authentication fails
     */
    @Override
    public LoginResult login(LoginCommand command) {
        log.info("Attempting login for email: {}", command.email());
        Email email = new Email(command.email());

        Account account = loadAccountByEmailPort.loadAccountByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Login failed: account with email {} not found", command.email());
                    return new InvalidCredentialsException();
                });

        if (!passwordEncoderPort.matches(command.passwordHash(), account.getPasswordHash())) {
            log.warn("Login failed: password mismatch for email {}", command.email());
            throw new InvalidCredentialsException();
        }

        log.info("Successfully authenticated email: {}", command.email());
        return LoginResult.builder()
                .userId(account.getAccountId().value())
                .publicKey(account.getPublicKey())
                .encryptedPrivateKey(account.getEncryptedPrivateKey())
                .build();
    }
}
