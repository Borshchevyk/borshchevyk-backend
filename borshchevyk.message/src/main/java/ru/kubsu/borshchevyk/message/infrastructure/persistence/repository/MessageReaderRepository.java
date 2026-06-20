package ru.kubsu.borshchevyk.message.infrastructure.persistence.repository;
import ru.kubsu.borshchevyk.message.domain.exception.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.MessageReaderEntity;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.MessageReaderId;

import java.util.List;
import java.util.UUID;


@Repository
public interface MessageReaderRepository extends JpaRepository<MessageReaderEntity, MessageReaderId> {
    List<MessageReaderEntity> findByMessageId(UUID messageId);
}
