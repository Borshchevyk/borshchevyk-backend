package ru.kubsu.borshchevyk.media.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.media.application.dto.command.CompleteUploadCommand;
import ru.kubsu.borshchevyk.media.application.dto.command.GetAttachmentUrlCommand;
import ru.kubsu.borshchevyk.media.application.dto.command.RequestUploadUrlCommand;
import ru.kubsu.borshchevyk.media.application.dto.command.ValidateAttachmentsCommand;
import ru.kubsu.borshchevyk.media.application.dto.response.AttachmentUrlResult;
import ru.kubsu.borshchevyk.media.application.dto.response.UploadUrlResult;
import ru.kubsu.borshchevyk.media.application.dto.response.ValidateAttachmentsResult;
import ru.kubsu.borshchevyk.media.application.port.in.*;
import ru.kubsu.borshchevyk.media.application.port.out.AttachmentPort;
import ru.kubsu.borshchevyk.media.application.port.out.S3Port;
import ru.kubsu.borshchevyk.media.domain.exception.AttachmentNotFoundException;
import ru.kubsu.borshchevyk.media.domain.exception.ForbiddenActionException;
import ru.kubsu.borshchevyk.media.domain.model.Attachment;
import ru.kubsu.borshchevyk.media.domain.model.AttachmentStatus;
import ru.kubsu.borshchevyk.media.domain.model.value.AttachmentId;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentService implements RequestUploadUrlUseCase, CompleteUploadUseCase, GetAttachmentUrlUseCase, ValidateAttachmentsUseCase, SoftDeleteUseCase {

    private final AttachmentPort attachmentPort;
    private final S3Port s3Port;

    @Override
    @Transactional
    public UploadUrlResult requestUploadUrl(RequestUploadUrlCommand command) {
        log.info("Requesting upload URL for user {} type {}", command.getUploaderId(), command.getType());

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
                .status(AttachmentStatus.INITIALIZED)
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
            throw new ForbiddenActionException("Only the uploader can complete the upload");
        }

        if (attachment.getStatus() != AttachmentStatus.INITIALIZED && attachment.getStatus() != AttachmentStatus.UPLOADING) {
            throw new IllegalStateException("Attachment is in invalid state: " + attachment.getStatus());
        }

        // Verify that object actually exists in S3
        boolean exists = s3Port.checkObjectExists(attachment.getS3Key());
        if (!exists) {
            attachment.setStatus(AttachmentStatus.FAILED);
            attachment.setUpdatedAt(LocalDateTime.now());
            attachmentPort.save(attachment);
            throw new IllegalStateException("Object not found in S3 bucket. Key: " + attachment.getS3Key());
        }

        attachment.setStatus(AttachmentStatus.READY);
        attachment.setUpdatedAt(LocalDateTime.now());
        
        return attachmentPort.save(attachment);
    }

    @Override
    @Transactional(readOnly = true)
    public AttachmentUrlResult getAttachmentUrl(GetAttachmentUrlCommand command) {
        log.info("Getting download URL for attachment {} by user {}", command.getAttachmentId(), command.getRequesterId());

        Attachment attachment = attachmentPort.findById(new AttachmentId(command.getAttachmentId()))
                .orElseThrow(() -> new AttachmentNotFoundException("Attachment not found"));

        if (attachment.getStatus() != AttachmentStatus.READY && attachment.getStatus() != AttachmentStatus.DELETED) {
            throw new IllegalStateException("Attachment is not ready for download");
        }

        String downloadUrl = s3Port.generatePresignedGetUrl(attachment.getS3Key(), Duration.ofHours(1));

        return AttachmentUrlResult.builder()
                .url(downloadUrl)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ValidateAttachmentsResult validateAttachments(ValidateAttachmentsCommand command) {
        if (command.getAttachmentIds() == null || command.getAttachmentIds().isEmpty()) {
            return ValidateAttachmentsResult.builder().valid(true).attachments(new ArrayList<>()).build();
        }

        List<AttachmentId> ids = command.getAttachmentIds().stream().map(AttachmentId::new).toList();
        List<Attachment> attachments = attachmentPort.findAllById(ids);

        if (attachments.size() != command.getAttachmentIds().size()) {
            log.warn("Validation failed: some attachments not found. Expected: {}, Found: {}", command.getAttachmentIds().size(), attachments.size());
            return ValidateAttachmentsResult.builder().valid(false).attachments(new ArrayList<>()).build();
        }

        List<ValidateAttachmentsResult.AttachmentMetadata> metadataList = new ArrayList<>();

        for (Attachment attachment : attachments) {
            if (!attachment.getUploaderId().equals(command.getRequesterId())) {
                log.warn("Validation failed: attachment {} doesn't belong to user {}", attachment.getId().value(), command.getRequesterId());
                return ValidateAttachmentsResult.builder().valid(false).attachments(new ArrayList<>()).build();
            }
            if (attachment.getStatus() != AttachmentStatus.READY) {
                log.warn("Validation failed: attachment {} is not READY (status: {})", attachment.getId().value(), attachment.getStatus());
                return ValidateAttachmentsResult.builder().valid(false).attachments(new ArrayList<>()).build();
            }
            
            metadataList.add(ValidateAttachmentsResult.AttachmentMetadata.builder()
                    .id(attachment.getId().value())
                    .type(attachment.getType().name())
                    .originalFilename(attachment.getOriginalFilename())
                    .extension(attachment.getExtension())
                    .sizeBytes(attachment.getSizeBytes())
                    .build());
        }

        return ValidateAttachmentsResult.builder()
                .valid(true)
                .attachments(metadataList)
                .build();
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
