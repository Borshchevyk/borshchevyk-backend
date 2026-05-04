package ru.kubsu.borshchevyk.calls.application.port.out;

import ru.kubsu.borshchevyk.calls.domain.model.User;

import java.util.UUID;

public interface GetUserInfoPort {
    User getUserInfo(UUID userId);
}
