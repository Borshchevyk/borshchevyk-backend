package ru.kubsu.borshchevyk.user.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kubsu.borshchevyk.user.infrastructure.persistence.entity.PrivacySettingsEntity;

import java.util.UUID;

/**
 * Spring Data JPA repository for {@link PrivacySettingsEntity}.
 *
 * @author Aleksey Timko
 */
@Repository
public interface PrivacySettingsRepository extends JpaRepository<PrivacySettingsEntity, UUID> {
}
