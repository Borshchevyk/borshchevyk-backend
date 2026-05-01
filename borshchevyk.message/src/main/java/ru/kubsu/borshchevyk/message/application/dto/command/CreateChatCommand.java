package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.Builder;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatType;

import java.util.List;
import java.util.UUID;

/**
 * Command to create a new chat.
 *
 * @author Aleksey Timko
 */
@Builder
public record CreateChatCommand(
    UUID creatorId,
    ChatType type,
    String title,
    String description,
    boolean commentsEnabled,
    List<UUID> initialMemberIds
) {
}
