package ru.kubsu.borshchevyk.message.application.port.in;

import ru.kubsu.borshchevyk.message.application.dto.command.JoinChatByLinkCommand;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;

public interface JoinChatByLinkUseCase {
    Chat joinChatByLink(JoinChatByLinkCommand command);
}