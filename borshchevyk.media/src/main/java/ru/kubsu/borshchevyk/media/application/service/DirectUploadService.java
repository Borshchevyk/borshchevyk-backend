package ru.kubsu.borshchevyk.media.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.media.application.dto.command.UploadDirectAttachmentCommand;
import ru.kubsu.borshchevyk.media.application.port.in.UploadDirectAttachmentUseCase;
import ru.kubsu.borshchevyk.media.application.port.out.AttachmentPort;
import ru.kubsu.borshchevyk.media.application.port.out.S3Port;
import ru.kubsu.borshchevyk.media.domain.exception.InvalidAttachmentTypeException;
import ru.kubsu.borshchevyk.media.domain.model.Attachment;
import ru.kubsu.borshchevyk.media.domain.model.AttachmentStatus;
import ru.kubsu.borshchevyk.media.domain.model.AttachmentType;
import ru.kubsu.borshchevyk.media.domain.model.value.AttachmentId;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service for direct upload of specific attachment types (e.g., voices, circles).
 *
 * @author Aleksey Timko
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DirectUploadService implements UploadDirectAttachmentUseCase {

    private final AttachmentPort attachmentPort;
    private final S3Port s3Port;

    @Override
    @Transactional
    public Attachment uploadDirectAttachment(UploadDirectAttachmentCommand command) {
        log.info("Uploading direct attachment of type {} for user {}", command.getType(), command.getUploaderId());

        if (command.getType() != AttachmentType.VOICE && command.getType() != AttachmentType.CIRCLE) {
            throw new InvalidAttachmentTypeException("Only VOICE and CIRCLE types are allowed for direct upload");
        }

        String extensionPart = (command.getExtension() != null && !command.getExtension().isEmpty()) ? "." + command.getExtension() : "";
        String pathPrefix = command.getType() == AttachmentType.VOICE ? "voices/" : "circles/";
        String s3Key = pathPrefix + command.getUploaderId() + "/" + UUID.randomUUID() + extensionPart;

        Attachment attachment = Attachment.builder()
                .id(new AttachmentId(UUID.randomUUID()))
                .uploaderId(command.getUploaderId())
                .type(command.getType())
                .s3Key(s3Key)
                .originalFilename(command.getOriginalFilename())
                .extension(command.getExtension())
                .contentType(command.getContentType())
                .sizeBytes(command.getSizeBytes())
                .duration(command.getDuration())
                .status(AttachmentStatus.READY) // Ready immediately upon successful direct upload
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        s3Port.uploadFile(s3Key, command.getInputStream(), command.getSizeBytes(), command.getContentType());
        
        return attachmentPort.save(attachment);
    }
}
