package ru.kubsu.borshchevyk.sync.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kubsu.borshchevyk.sync.infrastructure.adapter.out.persistence.entity.SyncEventEntity;

import java.util.List;
import java.util.UUID;

/**
 * Repository for managing sync events in the database.
 *
 * @author Aleksey Timko
 */
@Repository
public interface SyncEventRepository extends JpaRepository<SyncEventEntity, UUID> {
    
    /**
     * Finds all events ordered by creation time.
     */
    List<SyncEventEntity> findAllByOrderByTimestampAsc();

    @org.springframework.data.jpa.repository.Query(
        value = "SELECT * FROM sync_event_journal WHERE (user_id = :userId OR user_id IS NULL) AND EXISTS (SELECT 1 FROM jsonb_each_text(vector_clock->'clocks') AS e(key, val) WHERE CAST(e.val AS BIGINT) > COALESCE(CAST(:clientClock::jsonb->'clocks'->>e.key AS BIGINT), 0)) ORDER BY created_at ASC", 
        nativeQuery = true)
    List<SyncEventEntity> findEventsAfterOrConcurrent(
        @org.springframework.data.repository.query.Param("clientClock") String clientClockJson,
        @org.springframework.data.repository.query.Param("userId") java.util.UUID userId,
        org.springframework.data.domain.Pageable pageable);
}
