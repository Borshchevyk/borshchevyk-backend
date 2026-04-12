package ru.kubsu.borshchevyk.media.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kubsu.borshchevyk.media.domain.model.AttachmentStatus;
import ru.kubsu.borshchevyk.media.infrastructure.persistence.entity.AttachmentEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AttachmentRepository extends JpaRepository<AttachmentEntity, UUID> {
    List<AttachmentEntity> findByStatusAndCreatedAtBefore(AttachmentStatus status, LocalDateTime createdAt);
}
