package ru.kubsu.borshchevyk.message.domain.model.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chat {
    private ChatId id;
    private ChatType type;
    private String title;
    private String description;
    private String inviteCode;
    private LocalDateTime createdAt;
    private boolean isDeleted;
}
