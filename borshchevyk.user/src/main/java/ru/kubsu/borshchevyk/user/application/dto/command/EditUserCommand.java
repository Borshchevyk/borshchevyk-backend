package ru.kubsu.borshchevyk.user.application.dto.command;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * Command object carrying data for updating an existing user's profile.
 *
 * @param userId the unique identifier of the user to edit
 * @param email  the new email address (optional)
 * @param tag    the new user tag (optional)
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@Slf4j
@Builder
public record EditUserCommand(String userId, String email, String tag) { }
