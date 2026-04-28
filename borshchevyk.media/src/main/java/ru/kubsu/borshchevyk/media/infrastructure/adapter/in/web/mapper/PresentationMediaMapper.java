package ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kubsu.borshchevyk.media.application.dto.response.ValidateAttachmentsResult;
import ru.kubsu.borshchevyk.media.domain.model.Attachment;
import ru.kubsu.borshchevyk.media.domain.model.value.AttachmentId;
import ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.dto.response.AttachmentResponse;
import ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.dto.response.ValidateAttachmentsResponse;

import java.util.UUID;

/**
 * Mapper for presentation layer DTOs in media service.
 *
 * @author Aleksey Timko
 */
@Mapper(componentModel = "spring")
public interface PresentationMediaMapper {

    @Mapping(target = "id", source = "id.value")
    AttachmentResponse toResponse(Attachment attachment);

    ValidateAttachmentsResponse.AttachmentMetadataResponse toMetadataResponse(ValidateAttachmentsResult.AttachmentMetadata metadata);

    default UUID map(AttachmentId value) {
        return value != null ? value.value() : null;
    }
}
