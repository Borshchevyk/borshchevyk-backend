package ru.kubsu.borshchevyk.media.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kubsu.borshchevyk.media.domain.model.Attachment;
import ru.kubsu.borshchevyk.media.domain.model.value.AttachmentId;
import ru.kubsu.borshchevyk.media.infrastructure.persistence.entity.AttachmentEntity;

import java.util.UUID;

/**
 * Mapper for converting between Attachment domain model and AttachmentEntity.
 *
 * @author Aleksey Timko
 */
@Mapper(componentModel = "spring")
public interface AttachmentMapper {
    @Mapping(target = "id.value", source = "id")
    Attachment toDomain(AttachmentEntity entity);

    @Mapping(target = "id", source = "id.value")
    AttachmentEntity toEntity(Attachment attachment);

    default UUID map(AttachmentId value) {
        return value != null ? value.value() : null;
    }
}
