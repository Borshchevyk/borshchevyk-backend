package ru.kubsu.borshchevyk.message.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.ChatMemberEntity;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.ChatMemberId;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMemberEntity, ChatMemberId> {
    List<ChatMemberEntity> findByChatId(UUID chatId);
}
