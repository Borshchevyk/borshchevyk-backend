package ru.kubsu.borshchevyk.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.auth.application.dto.command.LoginCommand;
import ru.kubsu.borshchevyk.auth.application.port.in.LoginUseCase;
import ru.kubsu.borshchevyk.auth.application.port.out.LoadAccountByEmailPort;
import ru.kubsu.borshchevyk.auth.application.port.out.PasswordEncoderPort;
import ru.kubsu.borshchevyk.auth.domain.exception.InvalidCredentialsException;
import ru.kubsu.borshchevyk.auth.domain.model.account.Account;
import ru.kubsu.borshchevyk.auth.domain.model.result.LoginResult;
import ru.kubsu.borshchevyk.auth.domain.model.value.Email;

@Service
@RequiredArgsConstructor
public class LoginService implements LoginUseCase {

    private final LoadAccountByEmailPort loadAccountByEmailPort;
    private final PasswordEncoderPort passwordEncoderPort;

    @Override
    public LoginResult login(LoginCommand command) {
        Email email = new Email(command.email());

        Account account = loadAccountByEmailPort.loadAccountByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        // Проверяем "AuthHash" через BCrypt (на сервере хранится BCrypt(AuthHash))
        if (!passwordEncoderPort.matches(command.passwordHash(), account.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        // Вместо токенов возвращаем данные для второго этапа аутентификации
        return LoginResult.builder()
                .userId(account.getAccountId().value())
                .publicKey(account.getPublicKey())
                .encryptedPrivateKey(account.getEncryptedPrivateKey())
                .build();
    }
}
