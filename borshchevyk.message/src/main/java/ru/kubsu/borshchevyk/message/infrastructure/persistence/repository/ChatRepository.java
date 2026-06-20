package ru.kubsu.borshchevyk.message.infrastructure.persistence.repository;
import ru.kubsu.borshchevyk.message.domain.exception.*;

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
           "WHERE TYPE(c) = PrivateChatEntity " +
           "AND c.isDeleted = false " +
           "AND cm1.userId = :userId1 " +
           "AND cm2.userId = :userId2")
    Optional<ChatEntity> findPrivateChatBetweenUsers(@Param("userId1") UUID userId1, @Param("userId2") UUID userId2);

    Optional<ChatEntity> findByInviteCode(String inviteCode);

    @Query("SELECT c FROM ChatEntity c WHERE TYPE(c) IN (GroupChatEntity, ChannelEntity) AND LOWER(TREAT(c AS GroupChatEntity).title) LIKE LOWER(CONCAT('%', :query, '%')) AND c.isDeleted = false " +
           "OR TYPE(c) IN (GroupChatEntity, ChannelEntity) AND LOWER(TREAT(c AS ChannelEntity).title) LIKE LOWER(CONCAT('%', :query, '%')) AND c.isDeleted = false")
    List<ChatEntity> searchPublicChats(@Param("query") String query);
}
