package ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kubsu.borshchevyk.media.domain.model.Attachment;
import ru.kubsu.borshchevyk.media.domain.model.value.AttachmentId;
import ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.dto.response.AttachmentResponse;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PresentationMediaMapper {

    @Mapping(target = "id", source = "id.value")
    AttachmentResponse toResponse(Attachment attachment);

    default UUID map(AttachmentId value) {
        return value != null ? value.value() : null;
    }
}
