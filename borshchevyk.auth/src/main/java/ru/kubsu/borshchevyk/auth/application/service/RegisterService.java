package ru.kubsu.borshchevyk.auth.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.auth.application.dto.command.RegisterCommand;
import ru.kubsu.borshchevyk.auth.application.port.in.RegisterUseCase;
import ru.kubsu.borshchevyk.auth.application.port.out.LoadAccountByEmailPort;
import ru.kubsu.borshchevyk.auth.application.port.out.PasswordEncoderPort;
import ru.kubsu.borshchevyk.auth.application.port.out.SaveAccountPort;
import ru.kubsu.borshchevyk.auth.application.port.out.UserRegisteredEventPublisherPort;
import ru.kubsu.borshchevyk.auth.domain.event.UserRegisteredEvent;
import ru.kubsu.borshchevyk.auth.domain.exception.UserAlreadyExistsException;
import ru.kubsu.borshchevyk.auth.domain.model.account.Account;
import ru.kubsu.borshchevyk.auth.domain.model.result.RegisterResult;
import ru.kubsu.borshchevyk.auth.domain.model.value.AccountId;
import ru.kubsu.borshchevyk.auth.domain.model.value.Email;
import ru.kubsu.borshchevyk.auth.domain.model.value.Tag;

import java.util.UUID;

/**
 * Service for user registration.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterService implements RegisterUseCase {

    private final LoadAccountByEmailPort loadAccountByEmailPort;
    private final SaveAccountPort saveAccountPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final UserRegisteredEventPublisherPort userRegisteredEventPublisherPort;

    /**
     * Registers a new user account.
     *
     * @param command the registration command containing account details
     * @return the registration result containing the new user ID
     * @throws UserAlreadyExistsException if an account with the same email already exists
     */
    @Override
    @Transactional
    public RegisterResult register(RegisterCommand command) {
        log.info("Attempting to register new user with email: {}", command.email());
        Email email = new Email(command.email());
        
        loadAccountByEmailPort.loadAccountByEmail(email)
                .ifPresent(account -> {
                    log.warn("Registration failed: account with email {} already exists", command.email());
                    throw new UserAlreadyExistsException(email.getValue());
                });

        Tag tag = new Tag(command.tag());
        AccountId accountId = new AccountId(UUID.randomUUID());
        String encodedPassword = passwordEncoderPort.encode(command.passwordHash());

        Account newAccount = Account.builder()
                .accountId(accountId)
                .email(email)
                .tag(tag)
                .passwordHash(encodedPassword)
                .publicKey(command.publicKey())
                .encryptedPrivateKey(command.encryptedPrivateKey())
                .build();

        saveAccountPort.saveAccount(newAccount);

        userRegisteredEventPublisherPort.publish(UserRegisteredEvent.builder()
                .userId(accountId.value())
                .email(email.getValue())
                .tag(tag.getValue())
                .firstName(command.firstName())
                .build());

        log.info("Successfully registered new user with ID: {}", accountId.value());
        return RegisterResult.builder()
                .userId(accountId.value())
                .build();
    }
}
