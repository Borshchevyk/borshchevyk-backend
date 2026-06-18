package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.CountUnreadNotificationsPort;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.NotificationRepository;

@Component
@RequiredArgsConstructor
public class CountUnreadNotificationsAdapter implements CountUnreadNotificationsPort {

    private final NotificationRepository repository;

    @Override
    public long countUnreadByUserId(UserId userId) {
        return repository.countByUserIdAndIsReadFalse(userId.value());
    }
}
