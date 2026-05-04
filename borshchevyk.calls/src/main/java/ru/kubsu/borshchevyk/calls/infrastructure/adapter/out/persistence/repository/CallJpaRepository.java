package ru.kubsu.borshchevyk.calls.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.out.persistence.entity.CallJpaEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for Call entities.
 */
public interface CallJpaRepository extends JpaRepository<CallJpaEntity, UUID> {
    Optional<CallJpaEntity> findByRoomId(String roomId);
}
