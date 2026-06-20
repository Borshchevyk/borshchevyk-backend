package ru.kubsu.borshchevyk.message.infrastructure.persistence.repository;
import ru.kubsu.borshchevyk.message.domain.exception.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.ChatMemberEntity;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.ChatMemberId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMemberEntity, ChatMemberId> {
    List<ChatMemberEntity> findByChatId(UUID chatId);
    Page<ChatMemberEntity> findByChatId(UUID chatId, Pageable pageable);
    List<ChatMemberEntity> findByUserId(UUID userId);
    List<ChatMemberEntity> findByChatIdIn(List<UUID> chatIds);

    @Query("SELECT cm.userId FROM ChatMemberEntity cm JOIN MessageEntity m ON cm.lastReadMessageId = m.id WHERE cm.chatId = :chatId AND m.createdAt >= :messageCreatedAt")
    List<UUID> findReadersOfMessage(@Param("chatId") UUID chatId, @Param("messageCreatedAt") LocalDateTime messageCreatedAt);
}
