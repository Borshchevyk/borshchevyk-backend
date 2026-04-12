package ru.kubsu.borshchevyk.media.application.port.out;

import ru.kubsu.borshchevyk.media.domain.model.Attachment;
import ru.kubsu.borshchevyk.media.domain.model.value.AttachmentId;

import java.util.Optional;

public interface AttachmentPort {
    Attachment save(Attachment attachment);
    Optional<Attachment> findById(AttachmentId id);
}
