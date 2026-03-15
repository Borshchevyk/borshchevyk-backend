package ru.kubsu.borshchevyk.user.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.user.application.port.out.SaveUserPort;
import ru.kubsu.borshchevyk.user.domain.model.user.User;
import ru.kubsu.borshchevyk.user.infrastructure.persistence.entity.UserJpaEntity;
import ru.kubsu.borshchevyk.user.infrastructure.persistence.mapper.UserPersistenceMapper;
import ru.kubsu.borshchevyk.user.infrastructure.persistence.repository.UserSpringRepository;

@Component
@RequiredArgsConstructor
public class SaveUserAdapter implements SaveUserPort {

    private final UserSpringRepository userRepository;
    private final UserPersistenceMapper userMapper;

    @Override
    public void saveUser(User user) {
        UserJpaEntity entity = userMapper.toEntity(user);
        userRepository.save(entity);
    }
}
