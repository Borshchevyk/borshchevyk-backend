package ru.kubsu.borshchevyk.user.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.user.application.dto.command.EditUserCommand;
import ru.kubsu.borshchevyk.user.application.port.in.EditUserUseCase;
import ru.kubsu.borshchevyk.user.application.port.out.LoadUserPort;
import ru.kubsu.borshchevyk.user.application.port.out.SaveUserPort;
import ru.kubsu.borshchevyk.user.application.port.out.UserEventPublisherPort;
import ru.kubsu.borshchevyk.user.domain.event.UserUpdatedEvent;
import ru.kubsu.borshchevyk.user.domain.exception.UserNotFoundException;
import ru.kubsu.borshchevyk.user.domain.model.result.EditUserResult;
import ru.kubsu.borshchevyk.user.domain.model.user.User;
import ru.kubsu.borshchevyk.user.domain.model.value.Email;
import ru.kubsu.borshchevyk.user.domain.model.value.Tag;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EditUserService implements EditUserUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final UserEventPublisherPort userEventPublisherPort;

    @Override
    public EditUserResult editUser(EditUserCommand command) {
        UserId userId = new UserId(UUID.fromString(command.userId()));
        User user = loadUserPort.loadUserById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (command.email() != null && !command.email().isBlank()) {
            user.setEmail(new Email(command.email()));
        }

        if (command.tag() != null && !command.tag().isBlank()) {
            user.setTag(new Tag(command.tag()));
        }

        saveUserPort.saveUser(user);

        userEventPublisherPort.publishUpdated(UserUpdatedEvent.builder()
                .userId(userId.getValue())
                .email(user.getEmail() != null ? user.getEmail().getValue() : null)
                .tag(user.getTag() != null ? user.getTag().getValue() : null)
                .build());

        return EditUserResult.builder()
                .userId(user.getUserId().getValue().toString())
                .email(user.getEmail() != null ? user.getEmail().getValue() : null)
                .tag(user.getTag() != null ? user.getTag().getValue() : null)
                .build();
    }
}
