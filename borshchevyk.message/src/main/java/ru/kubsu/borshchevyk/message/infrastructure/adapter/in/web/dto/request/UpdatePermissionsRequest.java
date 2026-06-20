package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request;
import ru.kubsu.borshchevyk.message.domain.exception.*;


public record UpdatePermissionsRequest(
        Boolean canSendMessages,
        Boolean canDeleteMessages,
        Boolean canInviteUsers,
        Boolean canChangeInfo
) {}
