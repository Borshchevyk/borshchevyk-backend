package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

public interface CountUnreadNotificationsPort {
    long countUnreadByUserId(UserId userId);
}