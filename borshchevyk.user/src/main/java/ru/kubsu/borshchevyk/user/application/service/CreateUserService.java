package ru.kubsu.borshchevyk.user.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.user.application.port.in.CreateUserUseCase;
import ru.kubsu.borshchevyk.user.application.port.out.SaveUserPort;
import ru.kubsu.borshchevyk.user.domain.event.UserRegisteredEvent;
import ru.kubsu.borshchevyk.user.domain.model.user.User;
import ru.kubsu.borshchevyk.user.domain.model.value.Email;
import ru.kubsu.borshchevyk.user.domain.model.value.Tag;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

/**
 * Service for creating new user profiles.
 * Listens for user registration events and persists the data.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreateUserService implements CreateUserUseCase {

    private final SaveUserPort saveUserPort;

    /**
     * Creates a new user profile based on a registration event.
     *
     * @param event The registration event containing user details
     */
    @Override
    public void createUser(UserRegisteredEvent event) {
        log.info("Processing user creation for ID: {}", event.userId());
        
        User user = User.builder()
                .userId(new UserId(event.userId()))
                .email(new Email(event.email()))
                .tag(new Tag(event.tag()))
                .firstName(event.firstName())
                .build();
        
        saveUserPort.saveUser(user);
        log.info("Successfully created user profile for ID: {}", event.userId());
    }
}
