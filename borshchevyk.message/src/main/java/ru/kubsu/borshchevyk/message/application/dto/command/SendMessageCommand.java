package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kubsu.borshchevyk.message.domain.model.message.MessageSource;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendMessageCommand {
    private UUID chatId;
    private UUID authorId;
    private String text;
    private MessageSource source;
    private UUID forwardedFromChatId;
    private UUID forwardedFromUserId;
}
