package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.query.GenerateInviteLinkQuery;

public interface GenerateInviteLinkUseCase {
    String generateInviteLink(GenerateInviteLinkQuery query);
}