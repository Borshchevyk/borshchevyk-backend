package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request;

public record UpdatePermissionsRequest(
        Boolean canSendMessages,
        Boolean canDeleteMessages,
        Boolean canInviteUsers,
        Boolean canChangeInfo
) {}