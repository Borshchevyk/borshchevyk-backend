package ru.kubsu.borshchevyk.message.infrastructure.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortUserDto;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortChatDto;

import java.util.List;

/**
 * @author Aleksey Timko
 * @since 2026-05-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private String targetUserId;
    private MessageDto message;
    private ChatEventDto chatEvent;
    private CallEventDto callEvent;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatEventDto {
        private ShortChatDto chat;
        private String action;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CallEventDto {
        private String callId;
        private String eventType;
        private ShortUserDto initiator;
        private ShortUserDto actor;
        private String timestamp;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttachmentDto {
        private String id;
        private String type;
        private String originalFilename;
        private String extension;
        private Long sizeBytes;
        private Double duration;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageDto {
        private String id;
        private ShortChatDto chat;
        private ShortUserDto author;
        private String text;
        private String createdAt;
        private boolean isDeleted;
        private String status;
        private ShortChatDto forwardedFromChat;
        private ShortUserDto forwardedFromUser;
        private List<AttachmentDto> attachments;
    }
}