package ru.kubsu.borshchevyk.user.application.port.in;

import ru.kubsu.borshchevyk.user.application.dto.command.UpdateProfileCommand;
import ru.kubsu.borshchevyk.user.domain.model.user.User;

/**
 * UseCase for updating a user's profile information.
 *
 * @author Aleksey Timko
 */
public interface UpdateProfileUseCase {
    
    /**
     * Updates the profile of the user based on the provided command.
     *
     * @param command the command containing the updated profile information
     * @return the updated user profile
     */
    User updateProfile(UpdateProfileCommand command);
}

