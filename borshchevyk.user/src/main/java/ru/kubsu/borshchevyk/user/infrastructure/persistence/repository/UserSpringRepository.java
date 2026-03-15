package ru.kubsu.borshchevyk.user.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kubsu.borshchevyk.user.infrastructure.persistence.entity.UserJpaEntity;

import java.util.UUID;

/**
 * Spring Data JPA repository for {@link UserJpaEntity}.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
public interface UserSpringRepository extends JpaRepository<UserJpaEntity, UUID> {
}
