package ru.kubsu.borshchevyk.message.application.dto.query;

import ru.kubsu.borshchevyk.message.domain.model.value.UserId;

public record LoadUserChatsQuery(
        UserId userId
) { }