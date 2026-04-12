package ru.kubsu.borshchevyk.message.infrastructure.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.MessageEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {
    Page<MessageEntity> findByChatIdOrderByCreatedAtDesc(UUID chatId, Pageable pageable);

    Page<MessageEntity> findByChatIdAndParentMessageIdOrderByCreatedAtAsc(UUID chatId, UUID parentMessageId, Pageable pageable);

    int countByChatIdAndPinnedAtIsNotNull(UUID chatId);
    
    List<MessageEntity> findByChatIdAndPinnedAtIsNotNullOrderByPinnedAtDesc(UUID chatId);
}
