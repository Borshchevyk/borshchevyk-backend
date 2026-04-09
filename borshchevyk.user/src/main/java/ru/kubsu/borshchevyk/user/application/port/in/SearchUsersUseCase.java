package ru.kubsu.borshchevyk.user.application.port.in;

import ru.kubsu.borshchevyk.user.application.dto.command.SearchUsersCommand;
import ru.kubsu.borshchevyk.user.domain.model.user.User;
import java.util.List;

public interface SearchUsersUseCase {
    List<User> searchUsers(SearchUsersCommand command);
}
