package ru.kubsu.borshchevyk.media.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.media.application.dto.command.CompleteUploadCommand;
import ru.kubsu.borshchevyk.media.application.dto.command.RequestUploadUrlCommand;
import ru.kubsu.borshchevyk.media.application.dto.response.UploadUrlResult;
import ru.kubsu.borshchevyk.media.application.port.in.CompleteUploadUseCase;
import ru.kubsu.borshchevyk.media.application.port.in.RequestUploadUrlUseCase;
import ru.kubsu.borshchevyk.media.application.port.out.AttachmentPort;
import ru.kubsu.borshchevyk.media.application.port.out.S3Port;
import ru.kubsu.borshchevyk.media.domain.exception.AttachmentNotFoundException;
import ru.kubsu.borshchevyk.media.domain.model.Attachment;
import ru.kubsu.borshchevyk.media.domain.model.AttachmentStatus;
import ru.kubsu.borshchevyk.media.domain.model.value.AttachmentId;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentService implements RequestUploadUrlUseCase, CompleteUploadUseCase {

    private final AttachmentPort attachmentPort;
    private final S3Port s3Port;

    @Override
    @Transactional
    public UploadUrlResult requestUploadUrl(RequestUploadUrlCommand command) {
        log.info("Requesting upload URL for user {}", command.getUploaderId());

        String s3Key = "attachments/" + command.getUploaderId() + "/" + UUID.randomUUID() + "." + command.getExtension();

        Attachment attachment = Attachment.builder()
                .id(new AttachmentId(UUID.randomUUID()))
                .uploaderId(command.getUploaderId())
                .type(command.getType())
                .s3Key(s3Key)
                .originalFilename(command.getOriginalFilename())
                .extension(command.getExtension())
                .contentType(command.getContentType())
                .sizeBytes(command.getSizeBytes())
                .status(AttachmentStatus.UPLOADING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        attachment = attachmentPort.save(attachment);

        String uploadUrl = s3Port.generatePresignedPutUrl(s3Key, command.getContentType(), Duration.ofMinutes(15));

        return UploadUrlResult.builder()
                .attachmentId(attachment.getId().value())
                .uploadUrl(uploadUrl)
                .s3Key(s3Key)
                .build();
    }

    @Override
    @Transactional
    public Attachment completeUpload(CompleteUploadCommand command) {
        log.info("Completing upload for attachment {} by user {}", command.getAttachmentId(), command.getRequesterId());

        Attachment attachment = attachmentPort.findById(new AttachmentId(command.getAttachmentId()))
                .orElseThrow(() -> new AttachmentNotFoundException("Attachment not found"));

        if (!attachment.getUploaderId().equals(command.getRequesterId())) {
            throw new IllegalArgumentException("Only the uploader can complete the upload");
        }

        if (attachment.getStatus() != AttachmentStatus.UPLOADING) {
            throw new IllegalStateException("Attachment is not in UPLOADING state");
        }

        // Verify that object actually exists in S3 (optional but recommended)
        boolean exists = s3Port.checkObjectExists(attachment.getS3Key());
        if (!exists) {
            attachment.setStatus(AttachmentStatus.FAILED);
            attachment.setUpdatedAt(LocalDateTime.now());
            attachmentPort.save(attachment);
            throw new IllegalStateException("Object not found in S3 bucket");
        }

        attachment.setStatus(AttachmentStatus.READY);
        attachment.setUpdatedAt(LocalDateTime.now());
        
        return attachmentPort.save(attachment);
    }
}
