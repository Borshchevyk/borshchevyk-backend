package ru.kubsu.borshchevyk.user.application.port.in;

import ru.kubsu.borshchevyk.user.application.dto.command.GetUsersBatchCommand;
import ru.kubsu.borshchevyk.user.domain.model.user.User;

import java.util.List;

/**
 * Port for retrieving a batch of users.
 *
 * @author Aleksey Timko
 */
public interface GetUsersBatchUseCase {

    /**
     * Retrieves a batch of users based on the specified criteria.
     *
     * @param command the command containing criteria for fetching the batch
     * @return a list of users matching the criteria
     */
    List<User> getUsersBatch(GetUsersBatchCommand command);
}

