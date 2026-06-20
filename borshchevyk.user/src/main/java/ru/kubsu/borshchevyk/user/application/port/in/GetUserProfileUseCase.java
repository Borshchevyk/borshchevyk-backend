package ru.kubsu.borshchevyk.user.application.port.in;

import ru.kubsu.borshchevyk.user.application.dto.command.GetUserProfileCommand;
import ru.kubsu.borshchevyk.user.domain.model.user.User;

/**
 * Port for retrieving a user's profile information.
 *
 * @author Aleksey Timko
 */
public interface GetUserProfileUseCase {

    /**
     * Retrieves a user profile based on the provided command criteria.
     *
     * @param command the command containing parameters to fetch the user profile
     * @return the requested user profile
     */
    User getUserProfile(GetUserProfileCommand command);
}

