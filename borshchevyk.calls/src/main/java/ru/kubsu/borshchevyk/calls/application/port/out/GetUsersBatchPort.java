package ru.kubsu.borshchevyk.calls.application.port.out;

import ru.kubsu.borshchevyk.calls.domain.model.User;

import java.util.List;
import java.util.UUID;

public interface GetUsersBatchPort {
    List<User> getUsersBatch(List<UUID> userIds);
}
