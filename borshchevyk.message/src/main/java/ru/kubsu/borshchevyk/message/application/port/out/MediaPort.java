package ru.kubsu.borshchevyk.message.application.port.out;

import java.util.List;
import java.util.UUID;

public interface MediaPort {
    boolean validateAttachments(List<UUID> attachmentIds, UUID userId);
}
