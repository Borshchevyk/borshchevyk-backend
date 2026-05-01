package ru.kubsu.borshchevyk.media.application.port.in;

import java.util.UUID;

/**
 * UseCase to mark an attachment as soft-deleted.
 *
 * @author Aleksey Timko
 */
public interface SoftDeleteUseCase {
    void softDelete(UUID attachmentId, UUID requesterId);
}
