package ru.kubsu.borshchevyk.auth.domain.model.result;

import lombok.Builder;
import java.util.UUID;

/**
 * Result of a registration request.
 *
 * @param userId the unique identifier assigned to the new user
 * @author Aleksey Timko
 */
@Builder
public record RegisterResult(UUID userId) {}