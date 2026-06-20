package ru.kubsu.borshchevyk.message.infrastructure.persistence.repository;
import ru.kubsu.borshchevyk.message.domain.exception.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.MessageEntity;

import java.util.List;
import java.util.UUID;


@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {
    Page<MessageEntity> findByChatIdOrderByCreatedAtDesc(UUID chatId, Pageable pageable);

    Page<MessageEntity> findByChatIdAndParentMessageIdOrderByCreatedAtAsc(UUID chatId, UUID parentMessageId, Pageable pageable);

    @Query("SELECT m FROM MessageEntity m " +
           "WHERE m.chatId = :chatId " +
           "AND m.parentMessageId IS NULL " +
           "AND m.isDeleted = false " +
           "AND m.id NOT IN (SELECT dm.messageId FROM DeletedMessageEntity dm WHERE dm.userId = :userId) " +
           "AND (cast(:historyClearedAt as timestamp) IS NULL OR m.createdAt > :historyClearedAt) " +
           "ORDER BY m.createdAt DESC")
    Page<MessageEntity> loadChatHistory(
            @Param("chatId") UUID chatId, 
            @Param("userId") UUID userId, 
            @Param("historyClearedAt") java.time.LocalDateTime historyClearedAt, 
            Pageable pageable);

    @Query("SELECT m FROM MessageEntity m " +
           "WHERE m.chatId = :chatId " +
           "AND m.parentMessageId = :parentMessageId " +
           "AND m.isDeleted = false " +
           "AND m.id NOT IN (SELECT dm.messageId FROM DeletedMessageEntity dm WHERE dm.userId = :userId) " +
           "ORDER BY m.createdAt ASC")
    Page<MessageEntity> loadMessageComments(
            @Param("chatId") UUID chatId, 
            @Param("parentMessageId") UUID parentMessageId,
            @Param("userId") UUID userId, 
            Pageable pageable);

    int countByChatIdAndPinnedAtIsNotNull(UUID chatId);
    
    List<MessageEntity> findByChatIdAndPinnedAtIsNotNullOrderByPinnedAtDesc(UUID chatId);

    @Query("SELECT DISTINCT m FROM MessageEntity m JOIN m.attachments a " +
           "WHERE m.chatId = :chatId " +
           "AND a.type = :type " +
           "AND m.isDeleted = false " +
           "AND m.id NOT IN (SELECT dm.messageId FROM DeletedMessageEntity dm WHERE dm.userId = :userId) " +
           "AND (cast(:historyClearedAt as timestamp) IS NULL OR m.createdAt > :historyClearedAt) " +
           "ORDER BY m.createdAt DESC")
    Page<MessageEntity> loadChatAttachments(
            @Param("chatId") UUID chatId,
            @Param("userId") UUID userId,
            @Param("type") String type,
            @Param("historyClearedAt") java.time.LocalDateTime historyClearedAt,
            Pageable pageable);

    @Query("SELECT COUNT(m) FROM MessageEntity m " +
           "WHERE m.chatId = :chatId " +
           "AND m.isDeleted = false " +
           "AND m.authorId != :userId " +
           "AND m.id NOT IN (SELECT dm.messageId FROM DeletedMessageEntity dm WHERE dm.userId = :userId) " +
           "AND (cast(:historyClearedAt as timestamp) IS NULL OR m.createdAt > :historyClearedAt) " +
           "AND (cast(:lastReadAt as timestamp) IS NULL OR m.createdAt > :lastReadAt)")
    long countUnreadMessages(
            @Param("chatId") UUID chatId,
            @Param("userId") UUID userId,
            @Param("historyClearedAt") java.time.LocalDateTime historyClearedAt,
            @Param("lastReadAt") java.time.LocalDateTime lastReadAt);
}
