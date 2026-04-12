package ru.kubsu.borshchevyk.media.application.port.out;

import ru.kubsu.borshchevyk.media.domain.model.Attachment;
import ru.kubsu.borshchevyk.media.domain.model.value.AttachmentId;
import ru.kubsu.borshchevyk.media.domain.model.AttachmentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttachmentPort {
    Attachment save(Attachment attachment);
    Optional<Attachment> findById(AttachmentId id);
    List<Attachment> findAllById(List<AttachmentId> ids);
    List<Attachment> findByStatusAndCreatedAtBefore(AttachmentStatus status, LocalDateTime createdAt);
    void delete(AttachmentId id);
}
