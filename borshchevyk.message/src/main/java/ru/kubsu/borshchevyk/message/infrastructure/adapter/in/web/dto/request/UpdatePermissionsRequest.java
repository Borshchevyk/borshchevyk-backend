package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request;

/**
 * @author Aleksey Timko
 */
public record UpdatePermissionsRequest(
        Boolean canSendMessages,
        Boolean canDeleteMessages,
        Boolean canInviteUsers,
        Boolean canChangeInfo
) {}