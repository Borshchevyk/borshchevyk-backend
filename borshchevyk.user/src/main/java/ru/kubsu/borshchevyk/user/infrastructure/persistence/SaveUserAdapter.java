package ru.kubsu.borshchevyk.user.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.user.application.port.out.SaveUserPort;
import ru.kubsu.borshchevyk.user.domain.model.user.User;
import ru.kubsu.borshchevyk.user.infrastructure.persistence.entity.UserJpaEntity;
import ru.kubsu.borshchevyk.user.infrastructure.persistence.mapper.UserPersistenceMapper;
import ru.kubsu.borshchevyk.user.infrastructure.persistence.repository.UserSpringRepository;

/**
 * Persistence adapter for saving user data to the database.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SaveUserAdapter implements SaveUserPort {

    private final UserSpringRepository userRepository;
    private final UserPersistenceMapper userMapper;

    /**
     * Saves or updates a user in the database.
     *
     * @param user the domain model of the user to save
     */
    @Override
    public void saveUser(User user) {
        log.debug("Saving user with ID: {}", user.getUserId());
        UserJpaEntity entity = userMapper.toEntity(user);
        userRepository.save(entity);
        log.info("Successfully saved user with ID: {}", user.getUserId());
    }
}
