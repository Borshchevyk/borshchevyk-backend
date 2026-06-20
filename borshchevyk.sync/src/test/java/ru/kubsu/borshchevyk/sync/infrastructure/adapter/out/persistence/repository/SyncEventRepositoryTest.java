package ru.kubsu.borshchevyk.sync.infrastructure.adapter.out.persistence.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.kubsu.borshchevyk.sync.domain.model.EventType;
import ru.kubsu.borshchevyk.sync.domain.model.VectorClock;
import ru.kubsu.borshchevyk.sync.infrastructure.adapter.out.persistence.entity.SyncEventEntity;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SyncEventRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("sync_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
    }

    @Autowired
    private SyncEventRepository repository;

    @Test
    void shouldSaveAndLoadVectorClockAsJsonb() {
        // Arrange
        UUID eventId = UUID.randomUUID();
        UUID entityId = UUID.randomUUID();
        VectorClock clock = new VectorClock(Map.of("server", 1L, "client-a", 5L));

        SyncEventEntity entity = new SyncEventEntity(
                eventId,
                entityId,
                null,
                EventType.MESSAGE_CREATED,
                "{\"text\":\"hello\"}",
                clock,
                Instant.now()
        );

        // Act
        repository.save(entity);
        repository.flush(); // Force write to DB

        // Assert
        SyncEventEntity loaded = repository.findById(eventId).orElseThrow();
        assertThat(loaded.getEntityId()).isEqualTo(entityId);
        
        // Verify the VectorClock was correctly deserialized from JSONB
        VectorClock loadedClock = loaded.getVectorClock();
        assertThat(loadedClock).isNotNull();
        assertThat(loadedClock.getClocks().get("server")).isEqualTo(1L);
        assertThat(loadedClock.getClocks().get("client-a")).isEqualTo(5L);
        assertThat(loadedClock.isConcurrent(new VectorClock(Map.of("server", 2L, "client-a", 4L)))).isTrue();
    }

    @Test
    void shouldFindEventsAfterOrConcurrentUsingNativeQuery() throws Exception {
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        UUID userId = UUID.randomUUID();

        // 1. Event before/equal (already seen)
        SyncEventEntity eventBefore = SyncEventEntity.builder()
                .id(UUID.randomUUID())
                .entityId(UUID.randomUUID())
                .userId(userId)
                .eventType(EventType.MESSAGE_CREATED)
                .payload("{}")
                .vectorClock(new VectorClock(Map.of("server", 1L, "client-a", 1L)))
                .timestamp(Instant.now().minusSeconds(10))
                .build();

        // 2. Event after (unseen)
        SyncEventEntity eventAfter = SyncEventEntity.builder()
                .id(UUID.randomUUID())
                .entityId(UUID.randomUUID())
                .userId(userId)
                .eventType(EventType.MESSAGE_CREATED)
                .payload("{}")
                .vectorClock(new VectorClock(Map.of("server", 3L, "client-a", 1L)))
                .timestamp(Instant.now().minusSeconds(5))
                .build();

        // 3. Event concurrent (unseen)
        SyncEventEntity eventConcurrent = SyncEventEntity.builder()
                .id(UUID.randomUUID())
                .entityId(UUID.randomUUID())
                .userId(userId)
                .eventType(EventType.MESSAGE_CREATED)
                .payload("{}")
                .vectorClock(new VectorClock(Map.of("server", 1L, "client-a", 2L)))
                .timestamp(Instant.now())
                .build();

        // 4. Event after but belongs to a different user (unseen, but wrong user)
        SyncEventEntity eventOtherUser = SyncEventEntity.builder()
                .id(UUID.randomUUID())
                .entityId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .eventType(EventType.MESSAGE_CREATED)
                .payload("{}")
                .vectorClock(new VectorClock(Map.of("server", 3L, "client-a", 1L)))
                .timestamp(Instant.now().minusSeconds(1))
                .build();

        // 5. Event after but has NULL user_id (unseen, system event, visible to all)
        SyncEventEntity eventNullUser = SyncEventEntity.builder()
                .id(UUID.randomUUID())
                .entityId(UUID.randomUUID())
                .userId(null)
                .eventType(EventType.MESSAGE_CREATED)
                .payload("{}")
                .vectorClock(new VectorClock(Map.of("server", 3L, "client-a", 1L)))
                .timestamp(Instant.now())
                .build();

        repository.save(eventBefore);
        repository.save(eventAfter);
        repository.save(eventConcurrent);
        repository.save(eventOtherUser);
        repository.save(eventNullUser);
        repository.flush();

        // Client clock is {"server": 2, "client-a": 1}
        VectorClock clientClock = new VectorClock(Map.of("server", 2L, "client-a", 1L));
        String clientClockJson = objectMapper.writeValueAsString(clientClock);

        java.util.List<SyncEventEntity> results = repository.findEventsAfterOrConcurrent(
                clientClockJson,
                userId,
                org.springframework.data.domain.PageRequest.of(0, 10)
        );

        assertThat(results)
                .extracting(SyncEventEntity::getId)
                .containsExactlyInAnyOrder(eventAfter.getId(), eventConcurrent.getId(), eventNullUser.getId())
                .doesNotContain(eventBefore.getId(), eventOtherUser.getId());
    }
}
