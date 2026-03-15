package ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.persistence.entity.AccountJpaEntity;

import java.util.Optional;
import java.util.UUID;

public interface AccountSpringRepository extends JpaRepository<AccountJpaEntity, UUID> {
    Optional<AccountJpaEntity> findByEmail(String email);
}
