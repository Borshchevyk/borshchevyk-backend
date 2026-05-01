package ru.kubsu.borshchevyk.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.auth.application.dto.command.RegisterCommand;
import ru.kubsu.borshchevyk.auth.application.port.in.RegisterUseCase;
import ru.kubsu.borshchevyk.auth.application.port.out.CreateSavedMessagesPort;
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
@Service
@RequiredArgsConstructor
public class RegisterService implements RegisterUseCase {

    private final LoadAccountByEmailPort loadAccountByEmailPort;
    private final SaveAccountPort saveAccountPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final UserRegisteredEventPublisherPort userRegisteredEventPublisherPort;
    private final CreateSavedMessagesPort createSavedMessagesPort;

    @Override
    @Transactional
    public RegisterResult register(RegisterCommand command) {
        Email email = new Email(command.email());
        
        loadAccountByEmailPort.loadAccountByEmail(email)
                .ifPresent(account -> {
                    throw new UserAlreadyExistsException(email.value());
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
                .email(email.value())
                .tag(tag.value())
                .firstName(command.firstName())
                .lastName(command.lastName())
                .build());

        createSavedMessagesPort.createSavedMessages(accountId);

        return RegisterResult.builder()
                .userId(accountId.value())
                .build();
    }
}
