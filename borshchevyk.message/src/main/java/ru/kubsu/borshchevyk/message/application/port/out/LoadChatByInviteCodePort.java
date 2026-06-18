package ru.kubsu.borshchevyk.message.application.port.out;

import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;

import java.util.Optional;

public interface LoadChatByInviteCodePort {
    Optional<Chat> findByInviteCode(String inviteCode);
}