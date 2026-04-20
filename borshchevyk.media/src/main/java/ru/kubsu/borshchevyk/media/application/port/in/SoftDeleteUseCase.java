package ru.kubsu.borshchevyk.media.application.port.in;

import java.util.UUID;

public interface SoftDeleteUseCase {
    void softDelete(UUID attachmentId, UUID requesterId);
}
