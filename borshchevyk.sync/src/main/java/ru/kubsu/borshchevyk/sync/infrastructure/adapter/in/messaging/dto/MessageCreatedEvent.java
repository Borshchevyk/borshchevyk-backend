package ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageCreatedEvent {
    private String id;
    private String chatId;
    private String authorId;
    private String text;
    private String createdAt;
    private String status;
    private List<String> targetUserIds;
    private List<String> attachmentIds;
}
