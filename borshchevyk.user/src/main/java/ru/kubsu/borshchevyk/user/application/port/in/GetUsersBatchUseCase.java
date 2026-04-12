package ru.kubsu.borshchevyk.user.application.port.in;

import ru.kubsu.borshchevyk.user.application.dto.command.GetUsersBatchCommand;
import ru.kubsu.borshchevyk.user.domain.model.user.User;

import java.util.List;

public interface GetUsersBatchUseCase {
    List<User> getUsersBatch(GetUsersBatchCommand command);
}
