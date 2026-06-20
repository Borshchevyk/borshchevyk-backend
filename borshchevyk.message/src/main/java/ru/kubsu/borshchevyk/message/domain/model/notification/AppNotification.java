package ru.kubsu.borshchevyk.message.domain.model.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class AppNotification {
    private UUID id;
    private UserId userId;
    private String title;
    private String body;
    private NotificationType type;
    private boolean isRead;
    private LocalDateTime createdAt;

    public void markAsRead() {
        this.isRead = true;
    }
}