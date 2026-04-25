package ru.kubsu.borshchevyk.message.infrastructure.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kubsu.borshchevyk.message.domain.model.message.Message;

import java.util.List;
import java.util.stream.Collectors;

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
        private String chatId;
        private String authorId;
        private String text;
        private String createdAt;
        private boolean isDeleted;
        private String status;
        private String forwardedFromChatId;
        private String forwardedFromUserId;
        private List<AttachmentDto> attachments;

        public static MessageDto from(Message message) {
            List<AttachmentDto> attachmentDtos = message.getAttachments() != null ?
                    message.getAttachments().stream()
                            .map(a -> new AttachmentDto(
                                    a.getId() != null ? a.getId().toString() : null, 
                                    a.getType(),
                                    a.getOriginalFilename(),
                                    a.getExtension(),
                                    a.getSizeBytes()
                            ))
                            .collect(Collectors.toList()) : null;

            return new MessageDto(
                message.getId() != null ? message.getId().value().toString() : null,
                message.getChatId() != null ? message.getChatId().value().toString() : null,
                message.getAuthorId() != null ? message.getAuthorId().value().toString() : null,
                message.getText(),
                message.getCreatedAt() != null ? message.getCreatedAt().toString() : null,
                message.isDeleted(),
                message.getStatus() != null ? message.getStatus().name() : null,
                message.getForwardedFromChatId() != null ? message.getForwardedFromChatId().value().toString() : null,
                message.getForwardedFromUserId() != null ? message.getForwardedFromUserId().value().toString() : null,
                attachmentDtos
            );
        }
    }
}
