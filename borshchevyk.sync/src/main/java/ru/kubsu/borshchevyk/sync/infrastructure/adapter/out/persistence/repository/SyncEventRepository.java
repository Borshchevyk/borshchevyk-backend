package ru.kubsu.borshchevyk.sync.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kubsu.borshchevyk.sync.infrastructure.adapter.out.persistence.entity.SyncEventEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing sync events in the database.
 *
 * @author Aleksey Timko
 */
@Repository
public interface SyncEventRepository extends JpaRepository<SyncEventEntity, UUID> {
    
    List<SyncEventEntity> findByTargetUserIdAndSequenceNumberGreaterThanOrderBySequenceNumberAsc(
            UUID targetUserId, Long sequenceNumber, Pageable pageable);

    Optional<SyncEventEntity> findFirstByTargetUserIdOrderBySequenceNumberDesc(UUID targetUserId);
}