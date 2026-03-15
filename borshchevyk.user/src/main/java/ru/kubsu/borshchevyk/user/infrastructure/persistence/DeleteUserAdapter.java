package ru.kubsu.borshchevyk.user.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.user.application.port.out.DeleteUserPort;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;
import ru.kubsu.borshchevyk.user.infrastructure.persistence.repository.UserSpringRepository;

@Component
@RequiredArgsConstructor
public class DeleteUserAdapter implements DeleteUserPort {

    private final UserSpringRepository userRepository;

    @Override
    public void deleteUser(UserId userId) {
        userRepository.deleteById(userId.getValue());
    }
}
