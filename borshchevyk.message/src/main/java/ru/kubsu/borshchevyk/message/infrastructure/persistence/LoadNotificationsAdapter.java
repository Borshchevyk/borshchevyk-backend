package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.LoadNotificationsPort;
import ru.kubsu.borshchevyk.message.domain.model.notification.AppNotification;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.AppNotificationEntity;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.NotificationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LoadNotificationsAdapter implements LoadNotificationsPort {

    private final NotificationRepository repository;

    @Override
    public List<AppNotification> findByUserId(UserId userId, int limit) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId.value()).stream()
                .limit(limit)
                .map(this::mapToDomain)
                .collect(Collectors.toList());
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
