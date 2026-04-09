package ru.kubsu.borshchevyk.user.application.port.in;

import ru.kubsu.borshchevyk.user.application.dto.command.UpdateProfileCommand;
import ru.kubsu.borshchevyk.user.domain.model.user.User;

public interface UpdateProfileUseCase {
    User updateProfile(UpdateProfileCommand command);
}
