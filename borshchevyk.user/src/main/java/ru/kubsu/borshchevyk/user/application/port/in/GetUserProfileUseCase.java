package ru.kubsu.borshchevyk.user.application.port.in;

import ru.kubsu.borshchevyk.user.application.dto.command.GetUserProfileCommand;
import ru.kubsu.borshchevyk.user.domain.model.user.User;

/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
public interface GetUserProfileUseCase {
    User getUserProfile(GetUserProfileCommand command);
}

