package ru.kubsu.borshchevyk.user.application.port.out;

import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

public interface DeleteUserPort {
    void deleteUser(UserId userId);
}
