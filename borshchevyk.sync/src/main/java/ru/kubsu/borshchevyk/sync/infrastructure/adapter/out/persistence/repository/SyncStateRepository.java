package ru.kubsu.borshchevyk.sync.infrastructure.adapter.out.persistence.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.kubsu.borshchevyk.sync.infrastructure.adapter.out.persistence.entity.SyncStateEntity;

import java.util.Optional;

@Repository
public interface SyncStateRepository extends JpaRepository<SyncStateEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SyncStateEntity s WHERE s.id = 1")
    Optional<SyncStateEntity> findLockedState();
}
