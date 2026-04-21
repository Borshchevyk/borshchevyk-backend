package ru.kubsu.borshchevyk.media.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.media.application.dto.command.GetAttachmentUrlCommand;
import ru.kubsu.borshchevyk.media.application.dto.command.UploadAttachmentCommand;
import ru.kubsu.borshchevyk.media.application.dto.command.ValidateAttachmentsCommand;
import ru.kubsu.borshchevyk.media.application.dto.response.AttachmentUrlResult;
import ru.kubsu.borshchevyk.media.application.dto.response.ValidateAttachmentsResult;
import ru.kubsu.borshchevyk.media.application.port.in.*;
import ru.kubsu.borshchevyk.media.application.port.out.AttachmentPort;
import ru.kubsu.borshchevyk.media.application.port.out.S3Port;
import ru.kubsu.borshchevyk.media.domain.exception.AttachmentNotFoundException;
import ru.kubsu.borshchevyk.media.domain.exception.ForbiddenActionException;
import ru.kubsu.borshchevyk.media.domain.model.Attachment;
import ru.kubsu.borshchevyk.media.domain.model.AttachmentStatus;
import ru.kubsu.borshchevyk.media.domain.model.value.AttachmentId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentService implements UploadAttachmentUseCase, GetAttachmentUrlUseCase, ValidateAttachmentsUseCase, SoftDeleteUseCase {

    private final AttachmentPort attachmentPort;
    private final S3Port s3Port;

    @Override
    @Transactional
    public Attachment uploadAttachment(UploadAttachmentCommand command) {
        log.info("Uploading file for user {} type {}", command.getUploaderId(), command.getType());

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
                .width(command.getWidth())
                .height(command.getHeight())
                .duration(command.getDuration())
                .status(AttachmentStatus.UPLOADING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        attachment = attachmentPort.save(attachment);

        try {
            s3Port.uploadFile(s3Key, command.getInputStream(), command.getSizeBytes(), command.getContentType());
            attachment.setStatus(AttachmentStatus.READY);
            attachment.setUpdatedAt(LocalDateTime.now());
            return attachmentPort.save(attachment);
        } catch (Exception e) {
            log.error("Failed to upload attachment {} to S3", attachment.getId().value(), e);
            attachment.setStatus(AttachmentStatus.FAILED);
            attachment.setUpdatedAt(LocalDateTime.now());
            attachmentPort.save(attachment);
            throw new RuntimeException("Failed to upload attachment", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AttachmentUrlResult getAttachmentUrl(GetAttachmentUrlCommand command) {
        // Wait, since we are doing manual HTTP, there's no presigned URL capability without AWS SigV4.
        // We can just proxy it through the MediaController using standard Java stream.
        // We will return a URL pointing to the backend's download endpoint.
        // The URL will be constructed in the Controller, but we just validate here.
        log.info("Validating access for attachment {} by user {}", command.getAttachmentId(), command.getRequesterId());

        Attachment attachment = attachmentPort.findById(new AttachmentId(command.getAttachmentId()))
                .orElseThrow(() -> new AttachmentNotFoundException("Attachment not found"));

        if (attachment.getStatus() != AttachmentStatus.READY && attachment.getStatus() != AttachmentStatus.DELETED) {
            throw new IllegalStateException("Attachment is not ready for download");
        }

        // Return the internal URL pattern, the controller will map it
        String downloadUrl = "/api/v1/media/" + attachment.getId().value() + "/download";

        return AttachmentUrlResult.builder()
                .url(downloadUrl)
                .build();
    }

    public java.io.InputStream downloadAttachmentContent(UUID attachmentId, UUID requesterId) {
        Attachment attachment = attachmentPort.findById(new AttachmentId(attachmentId))
                .orElseThrow(() -> new AttachmentNotFoundException("Attachment not found"));

        if (attachment.getStatus() != AttachmentStatus.READY && attachment.getStatus() != AttachmentStatus.DELETED) {
            throw new IllegalStateException("Attachment is not ready for download");
        }

        return s3Port.downloadFile(attachment.getS3Key());
    }

    @Override
    @Transactional(readOnly = true)
    public ValidateAttachmentsResult validateAttachments(ValidateAttachmentsCommand command) {
        if (command.getAttachmentIds() == null || command.getAttachmentIds().isEmpty()) {
            return ValidateAttachmentsResult.builder().valid(true).build();
        }

        List<AttachmentId> ids = command.getAttachmentIds().stream().map(AttachmentId::new).toList();
        List<Attachment> attachments = attachmentPort.findAllById(ids);

        if (attachments.size() != command.getAttachmentIds().size()) {
            log.warn("Validation failed: some attachments not found. Expected: {}, Found: {}", command.getAttachmentIds().size(), attachments.size());
            return ValidateAttachmentsResult.builder().valid(false).build();
        }

        for (Attachment attachment : attachments) {
            if (!attachment.getUploaderId().equals(command.getRequesterId())) {
                log.warn("Validation failed: attachment {} doesn't belong to user {}", attachment.getId().value(), command.getRequesterId());
                return ValidateAttachmentsResult.builder().valid(false).build();
            }
            if (attachment.getStatus() != AttachmentStatus.READY) {
                log.warn("Validation failed: attachment {} is not READY (status: {})", attachment.getId().value(), attachment.getStatus());
                return ValidateAttachmentsResult.builder().valid(false).build();
            }
        }

        return ValidateAttachmentsResult.builder().valid(true).build();
    }

    @Override
    @Transactional
    public void softDelete(UUID attachmentId, UUID requesterId) {
        log.info("Soft deleting attachment {} by user {}", attachmentId, requesterId);
        Attachment attachment = attachmentPort.findById(new AttachmentId(attachmentId))
                .orElseThrow(() -> new AttachmentNotFoundException("Attachment not found"));

        if (!attachment.getUploaderId().equals(requesterId)) {
            throw new ForbiddenActionException("Only the owner can delete an attachment");
        }

        attachment.setStatus(AttachmentStatus.DELETED);
        attachment.setUpdatedAt(LocalDateTime.now());
        attachmentPort.save(attachment);
        log.info("Attachment {} marked as DELETED. File remains in S3.", attachmentId);
    }
}
