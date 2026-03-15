package ru.kubsu.borshchevyk.user.application.port.out;

import ru.kubsu.borshchevyk.user.domain.model.user.User;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

import java.util.Optional;

public interface LoadUserPort {
    Optional<User> loadUserById(UserId userId);
}
