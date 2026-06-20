package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.borshchevyk.message.application.port.out.CountUnreadNotificationsPort;
import ru.kubsu.borshchevyk.message.application.port.out.LoadNotificationPort;
import ru.kubsu.borshchevyk.message.application.port.out.LoadNotificationsPort;
import ru.kubsu.borshchevyk.message.application.port.out.SaveNotificationPort;
import ru.kubsu.borshchevyk.message.domain.model.notification.AppNotification;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Endpoints for managing user notifications")
public class NotificationController {
    private final LoadNotificationsPort loadNotificationsPort;
    private final CountUnreadNotificationsPort countUnreadNotificationsPort;
    private final SaveNotificationPort saveNotificationPort;
    private final LoadNotificationPort loadNotificationPort;



    @Operation(summary = "Get user notifications", description = "Retrieves recent notifications for the authenticated user")
    @GetMapping
    public List<AppNotification> getNotifications(
            @RequestHeader("X-User-Id") UUID requesterId,
            @RequestParam(defaultValue = "50") int limit) {
        return loadNotificationsPort.findByUserId(new UserId(requesterId), limit);
    }

    @Operation(summary = "Get unread count", description = "Retrieves the number of unread notifications")
    @GetMapping("/unread-count")
    public long getUnreadCount(
            @RequestHeader("X-User-Id") UUID requesterId) {
        return countUnreadNotificationsPort.countUnreadByUserId(new UserId(requesterId));
    }

    @Operation(summary = "Mark notification as read", description = "Marks a specific notification as read")
    @PatchMapping("/{notificationId}/read")
    public void markAsRead(
            @PathVariable UUID notificationId,
            @RequestHeader("X-User-Id") UUID requesterId) {
        loadNotificationPort.findById(notificationId).ifPresent(notification -> {
            if (notification.getUserId().value().equals(requesterId)) {
                notification.markAsRead();
                saveNotificationPort.save(notification);
            }
        });
    }
}