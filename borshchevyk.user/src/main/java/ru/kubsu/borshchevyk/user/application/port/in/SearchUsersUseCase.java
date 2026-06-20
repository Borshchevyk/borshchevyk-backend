package ru.kubsu.borshchevyk.user.application.port.in;

import ru.kubsu.borshchevyk.user.application.dto.command.SearchUsersCommand;
import ru.kubsu.borshchevyk.user.domain.model.user.User;
import java.util.List;

/**
 * UseCase for searching users by a query string.
 *
 * @author Aleksey Timko
 */
public interface SearchUsersUseCase {
    
    /**
     * Searches for users based on the criteria in the command.
     *
     * @param command the command containing search criteria
     * @return a list of matching users
     */
    List<User> searchUsers(SearchUsersCommand command);
}

