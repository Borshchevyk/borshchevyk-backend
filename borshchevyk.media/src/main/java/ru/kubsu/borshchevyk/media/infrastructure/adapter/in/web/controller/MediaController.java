package ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.kubsu.borshchevyk.media.application.dto.command.GetAttachmentUrlCommand;
import ru.kubsu.borshchevyk.media.application.dto.command.UploadAttachmentCommand;
import ru.kubsu.borshchevyk.media.application.dto.command.ValidateAttachmentsCommand;
import ru.kubsu.borshchevyk.media.application.dto.response.AttachmentUrlResult;
import ru.kubsu.borshchevyk.media.application.dto.response.ValidateAttachmentsResult;
import ru.kubsu.borshchevyk.media.application.port.in.*;
import ru.kubsu.borshchevyk.media.domain.model.Attachment;
import ru.kubsu.borshchevyk.media.domain.model.AttachmentType;
import ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.dto.request.ValidateAttachmentsRequest;
import ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.dto.response.AttachmentResponse;
import ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.dto.response.ValidateAttachmentsResponse;
import ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.mapper.PresentationMediaMapper;

import java.io.InputStream;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
@Tag(name = "Media", description = "Endpoints for handling media files")
public class MediaController {

    private final UploadAttachmentUseCase uploadAttachmentUseCase;
    private final GetAttachmentUrlUseCase getAttachmentUrlUseCase;
    private final ValidateAttachmentsUseCase validateAttachmentsUseCase;
    private final SoftDeleteUseCase softDeleteUseCase;
    private final PresentationMediaMapper presentationMediaMapper;

    @Operation(summary = "Upload file directly", description = "Uploads a file directly to the backend, which forwards it to S3.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AttachmentResponse uploadFile(
            @RequestHeader(value = "X-User-Id") UUID userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") AttachmentType type,
            @RequestParam(value = "width", required = false) Integer width,
            @RequestParam(value = "height", required = false) Integer height,
            @RequestParam(value = "duration", required = false) Double duration) {
        
        log.info("Requesting upload for user {} type {}", userId, type);

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
            }

            UploadAttachmentCommand command = UploadAttachmentCommand.builder()
                    .uploaderId(userId)
                    .type(type)
                    .contentType(file.getContentType())
                    .originalFilename(originalFilename)
                    .extension(extension)
                    .sizeBytes(file.getSize())
                    .width(width)
                    .height(height)
                    .duration(duration)
                    .inputStream(file.getInputStream())
                    .build();

            Attachment attachment = uploadAttachmentUseCase.uploadAttachment(command);
            return presentationMediaMapper.toResponse(attachment);
        } catch (Exception e) {
            log.error("Error processing file upload", e);
            throw new RuntimeException("Error processing file upload", e);
        }
    }

    @Operation(summary = "Get URL for downloading file", description = "Returns a URL to download the file. The URL points to the backend proxy.")
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

    @Operation(summary = "Download file content", description = "Streams the file content directly from S3 via the backend.")
    @GetMapping("/{attachmentId}/download")
    public ResponseEntity<InputStreamResource> downloadAttachment(
            @PathVariable UUID attachmentId,
            @RequestHeader(value = "X-User-Id", required = false) UUID userId) {
        
        log.info("Downloading attachment {} by user {}", attachmentId, userId);

        // We assume that the attachmentUrl was passed to the client. The client will call this.
        // It requires an authenticated user or a specific mechanism.
        // For simplicity and to match the generated URL from `getAttachmentUrl`, we stream it.
        try {
            ru.kubsu.borshchevyk.media.application.service.AttachmentService service = (ru.kubsu.borshchevyk.media.application.service.AttachmentService) uploadAttachmentUseCase;
            InputStream is = service.downloadAttachmentContent(attachmentId, userId);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachmentId + "\"")
                    .body(new InputStreamResource(is));
        } catch (Exception e) {
            log.error("Failed to download attachment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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

    @Operation(summary = "Validate attachments", description = "Internal endpoint to validate if attachments exist, belong to user and are READY.")
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
        
        return new ValidateAttachmentsResponse(result.isValid());
    }
}
