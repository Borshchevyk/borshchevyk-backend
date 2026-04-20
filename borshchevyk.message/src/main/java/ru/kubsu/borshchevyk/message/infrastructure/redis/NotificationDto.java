package ru.kubsu.borshchevyk.message.infrastructure.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private String targetUserId;
    private MessageDto message;
    private ChatEventDto chatEvent;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatEventDto {
        private String chatId;
        private String action;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageDto {
        private String id;
        private String chatId;
        private String authorId;
        private String text;
        private String createdAt;
        private boolean isDeleted;
        private String status;

        public static MessageDto from(Message message) {
            return new MessageDto(
                message.getId() != null ? message.getId().value().toString() : null,
                message.getChatId() != null ? message.getChatId().value().toString() : null,
                message.getAuthorId() != null ? message.getAuthorId().value().toString() : null,
                message.getText(),
                message.getCreatedAt() != null ? message.getCreatedAt().toString() : null,
                message.isDeleted(),
                message.getStatus() != null ? message.getStatus().name() : null
            );
        }
    }
}