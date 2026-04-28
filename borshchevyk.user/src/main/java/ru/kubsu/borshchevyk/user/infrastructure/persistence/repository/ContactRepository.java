package ru.kubsu.borshchevyk.user.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.user.infrastructure.persistence.entity.ContactEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<ContactEntity, UUID> {
    List<ContactEntity> findByOwnerId(UUID ownerId);
    boolean existsByOwnerIdAndContactUserId(UUID ownerId, UUID contactUserId);
    
    @Transactional
    void deleteByOwnerIdAndContactUserId(UUID ownerId, UUID contactUserId);
}
