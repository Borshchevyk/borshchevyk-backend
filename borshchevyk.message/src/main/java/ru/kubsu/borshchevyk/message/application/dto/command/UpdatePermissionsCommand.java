package ru.kubsu.borshchevyk.message.application.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePermissionsCommand {
    private UUID chatId;
    private UUID targetUserId;
    private UUID requesterId;
    private Boolean canSendMessages;
    private Boolean canDeleteMessages;
    private Boolean canInviteUsers;
    private Boolean canChangeInfo;
}