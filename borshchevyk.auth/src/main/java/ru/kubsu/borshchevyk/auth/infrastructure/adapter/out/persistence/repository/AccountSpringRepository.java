package ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.persistence.entity.AccountJpaEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link AccountJpaEntity}.
 */
public interface AccountSpringRepository extends JpaRepository<AccountJpaEntity, UUID> {
    /**
     * Finds an account by email.
     *
     * @param email the email to search for
     * @return an optional containing the found account, or empty if not found
     */
    Optional<AccountJpaEntity> findByEmail(String email);
}
