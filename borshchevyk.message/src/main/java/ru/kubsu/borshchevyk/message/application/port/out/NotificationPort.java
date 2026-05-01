package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.notification.AppNotification;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port for persistent notifications.
 *
 * @author Aleksey Timko
 */
public interface NotificationPort {
    AppNotification save(AppNotification notification);
    List<AppNotification> findByUserId(UserId userId, int limit);
    long countUnreadByUserId(UserId userId);
    Optional<AppNotification> findById(UUID id);
}
