package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.LoadNotificationPort;
import ru.kubsu.borshchevyk.message.domain.model.notification.AppNotification;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.AppNotificationEntity;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.NotificationRepository;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LoadNotificationAdapter implements LoadNotificationPort {

    private final NotificationRepository repository;

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
