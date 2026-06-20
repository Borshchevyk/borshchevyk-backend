package ru.kubsu.borshchevyk.message.infrastructure.persistence.repository;
import ru.kubsu.borshchevyk.message.domain.exception.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.DeletedMessageEntity;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.DeletedMessageId;

import java.util.UUID;


@Repository
public interface DeletedMessageRepository extends JpaRepository<DeletedMessageEntity, DeletedMessageId> {
    boolean existsByMessageIdAndUserId(UUID messageId, UUID userId);
}
