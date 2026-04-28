package ru.kubsu.borshchevyk.user.application.dto.command;

import lombok.Builder;

/**
 * Class documentation.
 *
 * @author Aleksey Timko
 */
public record LoadContactsCommand(
    String ownerId
) {
    @Builder
    public LoadContactsCommand {}
}

