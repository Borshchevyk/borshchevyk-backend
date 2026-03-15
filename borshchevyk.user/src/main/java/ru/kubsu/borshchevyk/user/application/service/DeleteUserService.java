package ru.kubsu.borshchevyk.user.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.user.application.port.in.DeleteUserUseCase;
import ru.kubsu.borshchevyk.user.application.port.out.DeleteUserPort;
import ru.kubsu.borshchevyk.user.application.port.out.UserEventPublisherPort;
import ru.kubsu.borshchevyk.user.domain.event.UserDeletedEvent;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteUserService implements DeleteUserUseCase {

    private final DeleteUserPort deleteUserPort;
    private final UserEventPublisherPort userEventPublisherPort;

    @Override
    public void deleteUser(String userId) {
        UserId id = new UserId(UUID.fromString(userId));
        deleteUserPort.deleteUser(id);
        
        userEventPublisherPort.publishDeleted(UserDeletedEvent.builder()
                .userId(id.getValue())
                .build());
    }
}
