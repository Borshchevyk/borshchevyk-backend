package ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.messaging.dto;

import lombok.Data;

import java.util.List;

@Data
public class MessageCreatedEvent {
    private String id;
    private String chatId;
    private String authorId;
    private String text;
    private String createdAt;
    private String status;
    private List<String> targetUserIds;
    private List<AttachmentInfo> attachments;

    @Data
    public static class AttachmentInfo {
        private String id;
        private String type;
        private String originalFilename;
        private String extension;
        private Long sizeBytes;
    }
}
