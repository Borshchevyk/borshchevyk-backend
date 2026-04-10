package ru.kubsu.borshchevyk.message.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.ChatEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, UUID> {
    List<ChatEntity> findByIdIn(List<UUID> ids);
}
