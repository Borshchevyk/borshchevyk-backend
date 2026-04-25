package ru.kubsu.borshchevyk.message.infrastructure.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortUserDto;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortChatDto;

import java.util.List;

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
        private ShortChatDto chat;
        private String action;
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