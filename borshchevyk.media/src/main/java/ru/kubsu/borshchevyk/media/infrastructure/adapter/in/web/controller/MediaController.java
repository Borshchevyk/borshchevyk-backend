package ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.borshchevyk.media.application.dto.command.CompleteUploadCommand;
import ru.kubsu.borshchevyk.media.application.dto.command.RequestUploadUrlCommand;
import ru.kubsu.borshchevyk.media.application.dto.response.UploadUrlResult;
import ru.kubsu.borshchevyk.media.application.port.in.CompleteUploadUseCase;
import ru.kubsu.borshchevyk.media.application.port.in.RequestUploadUrlUseCase;
import ru.kubsu.borshchevyk.media.domain.model.Attachment;
import ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.dto.request.RequestUploadUrlRequest;
import ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.dto.response.AttachmentResponse;
import ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.mapper.PresentationMediaMapper;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
@Tag(name = "Media", description = "Endpoints for handling media files")
public class MediaController {

    private final RequestUploadUrlUseCase requestUploadUrlUseCase;
    private final CompleteUploadUseCase completeUploadUseCase;
    private final PresentationMediaMapper presentationMediaMapper;

    @Operation(summary = "Get pre-signed URL for upload", description = "Generates a secure temporary link for the client to directly upload a file to S3 and returns attachment ID.")
    @PostMapping("/upload-url")
    public UploadUrlResult requestUploadUrl(
            @RequestHeader(value = "X-User-Id") UUID userId,
            @RequestBody RequestUploadUrlRequest request) {
        
        log.info("Requesting upload URL for user {}", userId);

        RequestUploadUrlCommand command = RequestUploadUrlCommand.builder()
                .uploaderId(userId)
                .type(request.type())
                .contentType(request.contentType())
                .originalFilename(request.originalFilename())
                .extension(request.extension())
                .sizeBytes(request.sizeBytes())
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
}
