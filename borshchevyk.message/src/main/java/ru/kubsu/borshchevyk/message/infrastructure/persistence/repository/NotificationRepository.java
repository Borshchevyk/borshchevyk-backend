package ru.kubsu.borshchevyk.message.infrastructure.persistence.repository;
import ru.kubsu.borshchevyk.message.domain.exception.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.AppNotificationEntity;

import java.util.List;
import java.util.UUID;


@Repository
public interface NotificationRepository extends JpaRepository<AppNotificationEntity, UUID> {
    List<AppNotificationEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
    long countByUserIdAndIsReadFalse(UUID userId);
}
