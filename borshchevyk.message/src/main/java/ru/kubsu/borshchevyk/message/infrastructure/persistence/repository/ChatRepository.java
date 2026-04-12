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
           "JOIN ChatMemberEntity cm1 ON c.id = cm1.chatId " +
           "JOIN ChatMemberEntity cm2 ON c.id = cm2.chatId " +
           "WHERE c.type = 'PRIVATE' " +
           "AND cm1.userId = :userId1 " +
           "AND cm2.userId = :userId2 " +
           "AND c.isDeleted = false")
    Optional<ChatEntity> findPrivateChatBetweenUsers(@Param("userId1") UUID userId1, @Param("userId2") UUID userId2);

    Optional<ChatEntity> findByInviteCode(String inviteCode);
}
