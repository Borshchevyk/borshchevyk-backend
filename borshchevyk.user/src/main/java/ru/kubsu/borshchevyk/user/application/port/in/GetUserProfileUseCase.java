package ru.kubsu.borshchevyk.user.application.port.in;

import ru.kubsu.borshchevyk.user.application.dto.command.GetUserProfileCommand;
import ru.kubsu.borshchevyk.user.domain.model.user.User;

public interface GetUserProfileUseCase {
    User getUserProfile(GetUserProfileCommand command);
}
