package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatType;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateChatCommand {
    private UUID creatorId;
    private ChatType type;
    private String title;
    private String description;
    private List<UUID> initialMemberIds;
}
