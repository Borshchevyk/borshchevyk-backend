package ru.kubsu.borshchevyk.user.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.user.application.port.in.CreateUserUseCase;
import ru.kubsu.borshchevyk.user.application.port.out.SaveUserPort;
import ru.kubsu.borshchevyk.user.domain.event.UserRegisteredEvent;
import ru.kubsu.borshchevyk.user.domain.model.user.User;
import ru.kubsu.borshchevyk.user.domain.model.value.Email;
import ru.kubsu.borshchevyk.user.domain.model.value.Tag;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

@Service
@RequiredArgsConstructor
public class CreateUserService implements CreateUserUseCase {

    private final SaveUserPort saveUserPort;

    @Override
    public void createUser(UserRegisteredEvent event) {
        User user = User.builder()
                .userId(new UserId(event.userId()))
                .email(new Email(event.email()))
                .tag(new Tag(event.tag()))
                .build();
        
        saveUserPort.saveUser(user);
    }
}
