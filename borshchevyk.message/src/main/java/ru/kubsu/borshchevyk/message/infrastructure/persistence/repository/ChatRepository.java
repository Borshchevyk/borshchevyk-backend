package ru.kubsu.borshchevyk.message.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.ChatEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, UUID> {
    List<ChatEntity> findByIdIn(List<UUID> ids);

    @Query("SELECT c FROM ChatEntity c " +
           "WHERE TYPE(c) = PrivateChatEntity " +
           "AND c.isDeleted = false " +
           "AND c.id IN (SELECT cm.chatId FROM ChatMemberEntity cm WHERE cm.userId = :userId1) " +
           "AND c.id IN (SELECT cm.chatId FROM ChatMemberEntity cm WHERE cm.userId = :userId2)")
    Optional<ChatEntity> findPrivateChatBetweenUsers(@Param("userId1") UUID userId1, @Param("userId2") UUID userId2);

    @Query(value = "SELECT * FROM chats WHERE invite_code = :inviteCode", nativeQuery = true)
    Optional<ChatEntity> findByInviteCode(@Param("inviteCode") String inviteCode);
}
