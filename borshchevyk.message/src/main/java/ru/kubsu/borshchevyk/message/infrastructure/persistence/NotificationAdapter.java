package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.NotificationPort;
import ru.kubsu.borshchevyk.message.domain.model.notification.AppNotification;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.AppNotificationEntity;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.NotificationRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter for managing persistent notifications.
 *
 * @author Aleksey Timko
 */
@Component
@RequiredArgsConstructor
public class NotificationAdapter implements NotificationPort {

    private final NotificationRepository repository;

    @Override
    public AppNotification save(AppNotification notification) {
        AppNotificationEntity entity = AppNotificationEntity.builder()
                .id(notification.getId())
                .userId(notification.getUserId().value())
                .title(notification.getTitle())
                .body(notification.getBody())
                .type(notification.getType())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
        
        entity = repository.save(entity);
        return mapToDomain(entity);
    }

    @Override
    public List<AppNotification> findByUserId(UserId userId, int limit) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId.value()).stream()
                .limit(limit)
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countUnreadByUserId(UserId userId) {
        return repository.countByUserIdAndIsReadFalse(userId.value());
    }

    @Override
    public Optional<AppNotification> findById(UUID id) {
        return repository.findById(id).map(this::mapToDomain);
    }

    private AppNotification mapToDomain(AppNotificationEntity entity) {
        return new AppNotification(
                entity.getId(),
                new UserId(entity.getUserId()),
                entity.getTitle(),
                entity.getBody(),
                entity.getType(),
                entity.isRead(),
                entity.getCreatedAt()
        );
    }
}
