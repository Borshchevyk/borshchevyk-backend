package ru.kubsu.borshchevyk.user.application.port.out;

import ru.kubsu.borshchevyk.user.domain.model.user.User;

public interface SaveUserPort {
    void saveUser(User user);
}
