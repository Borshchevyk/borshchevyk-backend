package ru.kubsu.borshchevyk.user.application.port.in;

import ru.kubsu.borshchevyk.user.application.dto.command.EditUserCommand;
import ru.kubsu.borshchevyk.user.domain.model.result.EditUserResult;

/**
 * Port for editing user details.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
public interface EditUserUseCase {
    /**
     * Edits user details.
     *
     * @param command the user edit command
     * @return the result of the user update operation
     */
    EditUserResult editUser(EditUserCommand command);
}
