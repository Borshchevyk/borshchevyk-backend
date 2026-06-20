package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.SaveNotificationPort;
import ru.kubsu.borshchevyk.message.domain.model.notification.AppNotification;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.AppNotificationEntity;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.NotificationRepository;

@Component
@RequiredArgsConstructor
public class SaveNotificationAdapter implements SaveNotificationPort {

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
