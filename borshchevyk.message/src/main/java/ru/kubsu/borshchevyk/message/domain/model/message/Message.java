package ru.kubsu.borshchevyk.message.domain.model.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.MessageId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    private MessageId id;
    private ChatId chatId;
    private UserId authorId;
    private String text;
    private LocalDateTime createdAt;
    private boolean isDeleted;
    private MessageSource source;
}
