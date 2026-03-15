package ru.kubsu.borshchevyk.user.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.user.application.port.out.LoadUserPort;
import ru.kubsu.borshchevyk.user.domain.model.user.User;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;
import ru.kubsu.borshchevyk.user.infrastructure.persistence.mapper.UserPersistenceMapper;
import ru.kubsu.borshchevyk.user.infrastructure.persistence.repository.UserSpringRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LoadUserAdapter implements LoadUserPort {

    private final UserSpringRepository userRepository;
    private final UserPersistenceMapper userMapper;

    @Override
    public Optional<User> loadUserById(UserId userId) {
        return userRepository.findById(userId.getValue())
                .map(userMapper::toDomain);
    }
}
