package ru.kubsu.borshchevyk.auth.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
@RequiredArgsConstructor
public class ChangePasswordService implements ChangePasswordUseCase {

    private final LoadAccountByEmailPort loadAccountByEmailPort;
    private final SaveAccountPort saveAccountPort;
    private final PasswordEncoderPort passwordEncoderPort;

    /**
     * Changes the password for an existing account.
     *
     * @param command the command containing change password data
     * @throws InvalidCredentialsException if the account is not found or the old password is incorrect
     */
    @Override
    public void changePassword(ChangePasswordCommand command) {
        log.info("Attempting to change password for user with email: {}", command.getEmail());

        Account account = loadAccountByEmailPort.loadAccountByEmail(new Email(command.getEmail()))
                .orElseThrow(() -> {
                    log.warn("Change password failed: account with email {} not found", command.getEmail());
                    return new InvalidCredentialsException();
                });

        if (!passwordEncoderPort.matches(command.getOldPassword(), account.getPasswordHash())) {
            log.warn("Change password failed: old password mismatch for email {}", command.getEmail());
            throw new InvalidCredentialsException();
        }

        account.setPasswordHash(passwordEncoderPort.encode(command.getNewPassword()));
        saveAccountPort.saveAccount(account);

        log.info("Successfully changed password for user with email: {}", command.getEmail());
    }
}
