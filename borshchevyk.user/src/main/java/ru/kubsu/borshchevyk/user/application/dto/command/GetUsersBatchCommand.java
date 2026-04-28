package ru.kubsu.borshchevyk.user.application.dto.command;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
@Builder
/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
public record GetUsersBatchCommand(
    List<UUID> userIds,
    String requesterId
) {}

