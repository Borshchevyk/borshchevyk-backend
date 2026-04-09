package ru.kubsu.borshchevyk.user.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.kubsu.borshchevyk.user.infrastructure.persistence.entity.UserJpaEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link UserJpaEntity}.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
public interface UserSpringRepository extends JpaRepository<UserJpaEntity, UUID> {
    Optional<UserJpaEntity> findByTag(String tag);

    @Query("SELECT u FROM UserJpaEntity u WHERE " +
           "LOWER(u.tag) LIKE LOWER(CONCAT(:query, '%')) OR " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "u.email = :query")
    List<UserJpaEntity> search(@Param("query") String query);
}
