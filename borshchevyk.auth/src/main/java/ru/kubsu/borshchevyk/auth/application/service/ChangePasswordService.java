package ru.kubsu.borshchevyk.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.auth.application.dto.command.ChangePasswordCommand;
import ru.kubsu.borshchevyk.auth.application.port.in.ChangePasswordUseCase;
import ru.kubsu.borshchevyk.auth.application.port.out.LoadAccountByEmailPort;
import ru.kubsu.borshchevyk.auth.application.port.out.PasswordEncoderPort;
import ru.kubsu.borshchevyk.auth.application.port.out.SaveAccountPort;
import ru.kubsu.borshchevyk.auth.domain.exception.InvalidCredentialsException;
import ru.kubsu.borshchevyk.auth.domain.model.account.Account;
import ru.kubsu.borshchevyk.auth.domain.model.value.Email;

@Service
@RequiredArgsConstructor
public class ChangePasswordService implements ChangePasswordUseCase {

    private final LoadAccountByEmailPort loadAccountByEmailPort;
    private final SaveAccountPort saveAccountPort;
    private final PasswordEncoderPort passwordEncoderPort;

    @Override
    public void changePassword(ChangePasswordCommand command) {
        Account account = loadAccountByEmailPort.loadAccountByEmail(new Email(command.getEmail()))
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoderPort.matches(command.getOldPassword(), account.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        account.setPasswordHash(passwordEncoderPort.encode(command.getNewPassword()));
        saveAccountPort.saveAccount(account);
    }
}
