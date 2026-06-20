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
import ru.kubsu.borshchevyk.media.domain.model.AttachmentType;
import ru.kubsu.borshchevyk.media.domain.model.value.AttachmentId;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.awt.image.BufferedImage;

import ru.kubsu.borshchevyk.media.application.dto.command.UploadAvatarCommand;
import net.coobird.thumbnailator.Thumbnails;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.Frame;

/**
 * Service implementing various use cases for managing attachments and media files.
 * Handles uploading, downloading, validation, and generation of thumbnails.
 *
 * @author Aleksey Timko
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentService implements RequestUploadUrlUseCase, CompleteUploadUseCase, GetAttachmentUrlUseCase, GetThumbnailUrlUseCase, ValidateAttachmentsUseCase, SoftDeleteUseCase, UploadAvatarUseCase {

    private final AttachmentPort attachmentPort;
    private final S3Port s3Port;

    @Override
    @Transactional
    public AttachmentUrlResult uploadAvatar(UploadAvatarCommand command) {
        log.info("Uploading avatar directly for user {}", command.uploaderId());

        String extensionPart = (command.extension() != null && !command.extension().isEmpty()) ? "." + command.extension() : "";
        String s3Key = "avatars/" + command.uploaderId() + "/" + UUID.randomUUID() + extensionPart;

        Attachment attachment = Attachment.builder()
                .id(new AttachmentId(UUID.randomUUID()))
                .uploaderId(command.uploaderId())
                .type(AttachmentType.AVATAR)
                .s3Key(s3Key)
                .originalFilename(command.originalFilename())
                .extension(command.extension())
                .contentType(command.contentType())
                .sizeBytes(command.sizeBytes())
                .status(AttachmentStatus.READY) // Avatar is ready immediately
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        s3Port.uploadFile(s3Key, command.inputStream(), command.sizeBytes(), command.contentType());
        attachment = attachmentPort.save(attachment);

        String localAvatarUrl = "/api/v1/media/avatars/" + attachment.getId().value();

        return AttachmentUrlResult.builder()
                .url(localAvatarUrl)
                .build();
    }

    @Override
    @Transactional
    public UploadUrlResult requestUploadUrl(RequestUploadUrlCommand command) {
        log.info("Requesting upload URL for user {} type {}", command.uploaderId(), command.type());

        String extensionPart = (command.extension() != null && !command.extension().isEmpty()) ? "." + command.extension() : "";
        String s3Key = "attachments/" + command.uploaderId() + "/" + UUID.randomUUID() + extensionPart;

        Attachment attachment = Attachment.builder()
                .id(new AttachmentId(UUID.randomUUID()))
                .uploaderId(command.uploaderId())
                .type(command.type())
                .s3Key(s3Key)
                .originalFilename(command.originalFilename())
                .extension(command.extension())
                .contentType(command.contentType())
                .sizeBytes(command.sizeBytes())
                .width(command.width())
                .height(command.height())
                .duration(command.duration())
                .status(AttachmentStatus.INITIALIZED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        attachment = attachmentPort.save(attachment);

        String uploadUrl = s3Port.generatePresignedPutUrl(s3Key, command.contentType(), Duration.ofMinutes(15));

        return UploadUrlResult.builder()
                .attachmentId(attachment.getId().value())
                .uploadUrl(uploadUrl)
                .s3Key(s3Key)
                .build();
    }

    @Override
    @Transactional
    public Attachment completeUpload(CompleteUploadCommand command) {
        log.info("Completing upload for attachment {} by user {}", command.attachmentId(), command.requesterId());

        Attachment attachment = attachmentPort.findById(new AttachmentId(command.attachmentId()))
                .orElseThrow(() -> new AttachmentNotFoundException("Attachment not found"));

        if (!attachment.getUploaderId().equals(command.requesterId())) {
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

        // Generate thumbnail for images and videos
        generateThumbnailIfNeeded(attachment);

        attachment.setStatus(AttachmentStatus.READY);
        attachment.setUpdatedAt(LocalDateTime.now());
        
        return attachmentPort.save(attachment);
    }
    
    public void generateThumbnailIfNeeded(Attachment attachment) {
        if (attachment.getType() == AttachmentType.PHOTO) {
            try {
                generateAndUploadImageThumbnail(attachment);
            } catch (Exception e) {
                log.error("Failed to generate image thumbnail for attachment {}", attachment.getId().value(), e);
            }
        } else if (attachment.getType() == AttachmentType.VIDEO || attachment.getType() == AttachmentType.CIRCLE) {
            try {
                generateAndUploadVideoThumbnail(attachment);
            } catch (Exception e) {
                log.error("Failed to generate video thumbnail for attachment {}", attachment.getId().value(), e);
            }
        }
    }

    private void generateAndUploadImageThumbnail(Attachment attachment) throws Exception {
        log.info("Generating thumbnail for image attachment {}", attachment.getId().value());
        
        try (InputStream originalStream = s3Port.downloadFile(attachment.getS3Key())) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            
            Thumbnails.of(originalStream)
                    .size(320, 320)
                    .keepAspectRatio(true)
                    .outputFormat("jpg")
                    .toOutputStream(outputStream);
            
            uploadThumbnailData(attachment, outputStream.toByteArray());
        }
    }

    private void generateAndUploadVideoThumbnail(Attachment attachment) throws Exception {
        log.info("Generating thumbnail for video attachment {}", attachment.getId().value());
        
        Path tempFile = Files.createTempFile("video_thumb_", "_" + attachment.getId().value());
        try {
            try (InputStream is = s3Port.downloadFile(attachment.getS3Key())) {
                Files.copy(is, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }

            try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(tempFile.toFile())) {
                grabber.start();
                // Try to grab a frame around 10% into the video, or at least a valid image frame
                int lengthInFrames = grabber.getLengthInVideoFrames();
                if (lengthInFrames > 10) {
                    grabber.setVideoFrameNumber(Math.min(10, lengthInFrames / 2));
                }
                
                Frame frame = null;
                for (int i = 0; i < 50; i++) {
                    frame = grabber.grabImage();
                    if (frame != null) break;
                }

                if (frame != null) {
                    try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
                        BufferedImage bufferedImage = converter.getBufferedImage(frame);
                        if (bufferedImage != null) {
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            Thumbnails.of(bufferedImage)
                                    .size(320, 320)
                                    .keepAspectRatio(true)
                                    .outputFormat("jpg")
                                    .toOutputStream(outputStream);
                            uploadThumbnailData(attachment, outputStream.toByteArray());
                        }
                    }
                } else {
                    log.warn("Failed to extract any image frame from video attachment {}", attachment.getId().value());
                }
                grabber.stop();
            }
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    private void uploadThumbnailData(Attachment attachment, byte[] thumbnailData) {
        String thumbnailKey = attachment.getS3Key().replace("attachments/", "thumbnails/").replace("circles/", "thumbnails/").replace("voices/", "thumbnails/");
        if (thumbnailKey.contains(".")) {
            thumbnailKey = thumbnailKey.substring(0, thumbnailKey.lastIndexOf(".")) + ".jpg";
        } else {
            thumbnailKey = thumbnailKey + ".jpg";
        }
        
        s3Port.uploadFile(thumbnailKey, new ByteArrayInputStream(thumbnailData), thumbnailData.length, "image/jpeg");
        
        attachment.setThumbnailKey(thumbnailKey);
        attachment.setThumbnailId(attachment.getId().value());
        log.info("Thumbnail generated and uploaded to {}", thumbnailKey);
    }

    @Override
    @Transactional(readOnly = true)
    public AttachmentUrlResult getAttachmentUrl(GetAttachmentUrlCommand command) {
        log.info("Getting download URL for attachment {} by user {}", command.attachmentId(), command.requesterId());

        Attachment attachment = attachmentPort.findById(new AttachmentId(command.attachmentId()))
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
    public AttachmentUrlResult getThumbnailUrl(GetAttachmentUrlCommand command) {
        log.info("Getting thumbnail URL for attachment {} by user {}", command.attachmentId(), command.requesterId());

        Attachment attachment = attachmentPort.findById(new AttachmentId(command.attachmentId()))
                .orElseThrow(() -> new AttachmentNotFoundException("Attachment not found"));

        if (attachment.getThumbnailKey() == null || attachment.getThumbnailKey().isEmpty()) {
            log.warn("Thumbnail not found for attachment {}, returning original URL as fallback", command.attachmentId());
            return getAttachmentUrl(command);
        }

        String downloadUrl = s3Port.generatePresignedGetUrl(attachment.getThumbnailKey(), Duration.ofHours(1));

        return AttachmentUrlResult.builder()
                .url(downloadUrl)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ValidateAttachmentsResult validateAttachments(ValidateAttachmentsCommand command) {
        if (command.attachmentIds() == null || command.attachmentIds().isEmpty()) {
            return ValidateAttachmentsResult.builder().valid(true).attachments(new ArrayList<>()).build();
        }

        List<AttachmentId> ids = command.attachmentIds().stream().map(AttachmentId::new).toList();
        List<Attachment> attachments = attachmentPort.findAllById(ids);

        if (attachments.size() != command.attachmentIds().size()) {
            log.warn("Validation failed: some attachments not found. Expected: {}, Found: {}", command.attachmentIds().size(), attachments.size());
            return ValidateAttachmentsResult.builder().valid(false).attachments(new ArrayList<>()).build();
        }

        List<ValidateAttachmentsResult.AttachmentMetadata> metadataList = new ArrayList<>();

        for (Attachment attachment : attachments) {
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
                    .duration(attachment.getDuration())
                    .thumbnailId(attachment.getThumbnailId())
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
