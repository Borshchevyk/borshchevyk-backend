package ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.borshchevyk.media.application.dto.command.CompleteUploadCommand;
import ru.kubsu.borshchevyk.media.application.dto.command.GetAttachmentUrlCommand;
import ru.kubsu.borshchevyk.media.application.dto.command.RequestUploadUrlCommand;
import ru.kubsu.borshchevyk.media.application.dto.command.ValidateAttachmentsCommand;
import ru.kubsu.borshchevyk.media.application.dto.response.AttachmentUrlResult;
import ru.kubsu.borshchevyk.media.application.dto.response.UploadUrlResult;
import ru.kubsu.borshchevyk.media.application.dto.response.ValidateAttachmentsResult;
import ru.kubsu.borshchevyk.media.application.port.in.*;
import ru.kubsu.borshchevyk.media.domain.model.Attachment;
import ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.dto.request.RequestUploadUrlRequest;
import ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.dto.request.ValidateAttachmentsRequest;
import ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.dto.response.AttachmentResponse;
import ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.dto.response.ValidateAttachmentsResponse;
import ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.mapper.PresentationMediaMapper;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
@Tag(name = "Media", description = "Endpoints for handling media files")
public class MediaController {

    private final RequestUploadUrlUseCase requestUploadUrlUseCase;
    private final CompleteUploadUseCase completeUploadUseCase;
    private final GetAttachmentUrlUseCase getAttachmentUrlUseCase;
    private final ValidateAttachmentsUseCase validateAttachmentsUseCase;
    private final SoftDeleteUseCase softDeleteUseCase;
    private final PresentationMediaMapper presentationMediaMapper;
    private final ru.kubsu.borshchevyk.media.application.port.in.UploadAvatarUseCase uploadAvatarUseCase;

    @Operation(summary = "Upload user avatar", description = "Uploads a user avatar directly and returns its URL.")
    @PostMapping(value = "/upload/avatar", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public AttachmentUrlResult uploadAvatar(
            @RequestHeader(value = "X-User-Id") UUID userId,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) throws java.io.IOException {
        
        log.info("Uploading avatar for user {}", userId);
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }

        ru.kubsu.borshchevyk.media.application.dto.command.UploadAvatarCommand command = ru.kubsu.borshchevyk.media.application.dto.command.UploadAvatarCommand.builder()
                .uploaderId(userId)
                .inputStream(file.getInputStream())
                .contentType(file.getContentType())
                .sizeBytes(file.getSize())
                .extension(extension)
                .originalFilename(originalFilename)
                .build();

        AttachmentUrlResult result = uploadAvatarUseCase.uploadAvatar(command);
        
        String absoluteUrl = org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(result.getUrl().substring("/api/v1/media".length())) // since ServletUriComponentsBuilder is relative to context path
                .build()
                .toUriString();
                
        return AttachmentUrlResult.builder().url(absoluteUrl).build();
    }

    @Operation(summary = "Get public avatar", description = "Redirects to a temporary S3 URL for displaying an avatar.")
    @GetMapping("/avatars/{attachmentId}")
    public void getAvatar(
            @PathVariable UUID attachmentId,
            jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        
        GetAttachmentUrlCommand command = GetAttachmentUrlCommand.builder()
                .attachmentId(attachmentId)
                .requesterId(null) // No requester needed for public avatars
                .build();

        AttachmentUrlResult result = getAttachmentUrlUseCase.getAttachmentUrl(command);
        response.sendRedirect(result.getUrl());
    }

    @Operation(summary = "Get pre-signed URL for upload", description = "Generates a secure temporary link for the client to directly upload a file to S3 and returns attachment ID.")
    @PostMapping("/upload-url")
    public UploadUrlResult requestUploadUrl(
            @RequestHeader(value = "X-User-Id") UUID userId,
            @RequestBody RequestUploadUrlRequest request) {
        
        log.info("Requesting upload URL for user {} type {}", userId, request.type());

        RequestUploadUrlCommand command = RequestUploadUrlCommand.builder()
                .uploaderId(userId)
                .type(request.type())
                .contentType(request.contentType())
                .originalFilename(request.originalFilename())
                .extension(request.extension())
                .sizeBytes(request.sizeBytes())
                .width(request.width())
                .height(request.height())
                .duration(request.duration())
                .build();

        return requestUploadUrlUseCase.requestUploadUrl(command);
    }

    @Operation(summary = "Complete upload", description = "Notifies the server that the client has finished uploading the file to S3.")
    @PutMapping("/{attachmentId}/complete")
    public AttachmentResponse completeUpload(
            @PathVariable UUID attachmentId,
            @RequestHeader(value = "X-User-Id") UUID userId) {
        
        log.info("Completing upload for attachment {} by user {}", attachmentId, userId);

        CompleteUploadCommand command = CompleteUploadCommand.builder()
                .attachmentId(attachmentId)
                .requesterId(userId)
                .build();

        Attachment attachment = completeUploadUseCase.completeUpload(command);
        return presentationMediaMapper.toResponse(attachment);
    }

    @Operation(summary = "Get pre-signed URL for download", description = "Generates a secure temporary link for the client to directly download a file from S3.")
    @GetMapping("/{attachmentId}/url")
    public AttachmentUrlResult getAttachmentUrl(
            @PathVariable UUID attachmentId,
            @RequestHeader(value = "X-User-Id") UUID userId) {

        log.info("Getting attachment URL for attachment {} by user {}", attachmentId, userId);

        GetAttachmentUrlCommand command = GetAttachmentUrlCommand.builder()
                .attachmentId(attachmentId)
                .requesterId(userId)
                .build();

        return getAttachmentUrlUseCase.getAttachmentUrl(command);
    }

    @Operation(summary = "Soft delete attachment", description = "Marks an attachment as DELETED. The file is not removed from S3 for history purposes.")
    @DeleteMapping("/{attachmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAttachment(
            @PathVariable UUID attachmentId,
            @RequestHeader(value = "X-User-Id") UUID userId) {
        
        log.info("Request to delete attachment {} by user {}", attachmentId, userId);
        softDeleteUseCase.softDelete(attachmentId, userId);
    }

    @Operation(summary = "Validate attachments", description = "Internal endpoint to validate if attachments exist, belong to user and are READY. Returns attachment metadata.")
    @PostMapping("/validate")
    public ValidateAttachmentsResponse validateAttachments(
            @RequestHeader(value = "X-User-Id") UUID userId,
            @RequestBody ValidateAttachmentsRequest request) {
        
        log.info("Validating attachments for user {}", userId);

        ValidateAttachmentsCommand command = ValidateAttachmentsCommand.builder()
                .attachmentIds(request.attachmentIds())
                .requesterId(userId)
                .build();

        ValidateAttachmentsResult result = validateAttachmentsUseCase.validateAttachments(command);
        
        List<ValidateAttachmentsResponse.AttachmentMetadataResponse> metadataResponses = null;
        if (result.getAttachments() != null) {
            metadataResponses = result.getAttachments().stream()
                    .map(m -> new ValidateAttachmentsResponse.AttachmentMetadataResponse(
                            m.getId(), 
                            m.getType(),
                            m.getOriginalFilename(),
                            m.getExtension(),
                            m.getSizeBytes()
                    ))
                    .toList();
        }
        
        return new ValidateAttachmentsResponse(result.isValid(), metadataResponses);
    }
}
