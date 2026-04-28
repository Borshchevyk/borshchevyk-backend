package ru.kubsu.borshchevyk.calls.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for Call entities.
 *
 * @author Aleksey Timko
 * @since 2026-04-25
 */
public interface CallRepository extends JpaRepository<CallEntity, UUID> {
    Optional<CallEntity> findByRoomId(String roomId);
}
