package ru.kubsu.borshchevyk.user.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kubsu.borshchevyk.user.infrastructure.persistence.entity.UserJpaEntity;

import java.util.UUID;

public interface UserSpringRepository extends JpaRepository<UserJpaEntity, UUID> {
}
