package ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDeletedEvent {
    private String messageId;
    private String deletedBy;
}
