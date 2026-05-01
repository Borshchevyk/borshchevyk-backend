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

/**
 * Service for changing user password.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Service
@RequiredArgsConstructor
public class ChangePasswordService implements ChangePasswordUseCase {

    private final LoadAccountByEmailPort loadAccountByEmailPort;
    private final SaveAccountPort saveAccountPort;
    private final PasswordEncoderPort passwordEncoderPort;

    @Override
    public void changePassword(ChangePasswordCommand command) {
        Account account = loadAccountByEmailPort.loadAccountByEmail(new Email(command.email()))
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoderPort.matches(command.oldPassword(), account.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        account.updatePasswordHash(passwordEncoderPort.encode(command.newPassword()));
        saveAccountPort.saveAccount(account);
    }
}
