package ru.kubsu.borshchevyk.message.domain.model.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Chat {
    private ChatId id;
    private ChatType type;
    private LocalDateTime createdAt;
    private boolean isDeleted;
}
