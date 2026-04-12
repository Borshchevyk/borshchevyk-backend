package ru.kubsu.borshchevyk.media.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.media.application.port.out.AttachmentPort;
import ru.kubsu.borshchevyk.media.domain.model.Attachment;
import ru.kubsu.borshchevyk.media.domain.model.value.AttachmentId;
import ru.kubsu.borshchevyk.media.infrastructure.persistence.entity.AttachmentEntity;
import ru.kubsu.borshchevyk.media.infrastructure.persistence.mapper.AttachmentMapper;
import ru.kubsu.borshchevyk.media.infrastructure.persistence.repository.AttachmentRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AttachmentAdapter implements AttachmentPort {

    private final AttachmentRepository attachmentRepository;
    private final AttachmentMapper attachmentMapper;

    @Override
    public Attachment save(Attachment attachment) {
        AttachmentEntity entity = attachmentMapper.toEntity(attachment);
        AttachmentEntity saved = attachmentRepository.save(entity);
        return attachmentMapper.toDomain(saved);
    }

    @Override
    public Optional<Attachment> findById(AttachmentId id) {
        return attachmentRepository.findById(id.value())
                .map(attachmentMapper::toDomain);
    }
}
