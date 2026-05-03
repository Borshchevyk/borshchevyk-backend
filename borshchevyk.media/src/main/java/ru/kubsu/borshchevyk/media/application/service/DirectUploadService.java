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
    private final AttachmentService attachmentService;

    @Override
    @Transactional
    public Attachment uploadDirectAttachment(UploadDirectAttachmentCommand command) {
        log.info("Uploading direct attachment of type {} for user {}", command.type(), command.uploaderId());

        if (command.type() != AttachmentType.VOICE && command.type() != AttachmentType.CIRCLE) {
            throw new InvalidAttachmentTypeException("Only VOICE and CIRCLE types are allowed for direct upload");
        }

        String extensionPart = (command.extension() != null && !command.extension().isEmpty()) ? "." + command.extension() : "";
        String pathPrefix = command.type() == AttachmentType.VOICE ? "voices/" : "circles/";
        String s3Key = pathPrefix + command.uploaderId() + "/" + UUID.randomUUID() + extensionPart;

        Attachment attachment = Attachment.builder()
                .id(new AttachmentId(UUID.randomUUID()))
                .uploaderId(command.uploaderId())
                .type(command.type())
                .s3Key(s3Key)
                .originalFilename(command.originalFilename())
                .extension(command.extension())
                .contentType(command.contentType())
                .sizeBytes(command.sizeBytes())
                .duration(command.duration())
                .status(AttachmentStatus.READY) // Ready immediately upon successful direct upload
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        s3Port.uploadFile(s3Key, command.inputStream(), command.sizeBytes(), command.contentType());
        
        attachmentService.generateThumbnailIfNeeded(attachment);
        
        return attachmentPort.save(attachment);
    }
}
