package ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.kubsu.borshchevyk.media.application.dto.command.UploadDirectAttachmentCommand;
import ru.kubsu.borshchevyk.media.application.port.in.UploadDirectAttachmentUseCase;
import ru.kubsu.borshchevyk.media.domain.model.Attachment;
import ru.kubsu.borshchevyk.media.domain.model.AttachmentType;
import ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.dto.response.AttachmentResponse;
import ru.kubsu.borshchevyk.media.infrastructure.adapter.in.web.mapper.PresentationMediaMapper;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/media/upload")
@RequiredArgsConstructor
@Tag(name = "Direct Media Upload", description = "Endpoints for direct file uploads like voice messages and circles")
public class DirectUploadController {

    private final UploadDirectAttachmentUseCase uploadDirectAttachmentUseCase;
    private final PresentationMediaMapper presentationMediaMapper;

    @Operation(summary = "Upload voice message", description = "Directly uploads a voice message and returns its metadata.")
    @PostMapping(value = "/voice", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AttachmentResponse uploadVoice(
            @RequestHeader(value = "X-User-Id") UUID userId,
            @Parameter(description = "Duration in seconds") @RequestParam(value = "duration", required = false) Double duration,
            @RequestParam("file") MultipartFile file) throws IOException {

        log.info("Uploading voice message for user {}, duration: {}", userId, duration);
        return uploadDirect(userId, file, AttachmentType.VOICE, duration);
    }

    @Operation(summary = "Upload circle video note", description = "Directly uploads a circle video note and returns its metadata.")
    @PostMapping(value = "/circle", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AttachmentResponse uploadCircle(
            @RequestHeader(value = "X-User-Id") UUID userId,
            @Parameter(description = "Duration in seconds") @RequestParam(value = "duration", required = false) Double duration,
            @RequestParam("file") MultipartFile file) throws IOException {

        log.info("Uploading circle for user {}, duration: {}", userId, duration);
        return uploadDirect(userId, file, AttachmentType.CIRCLE, duration);
    }

    private AttachmentResponse uploadDirect(UUID userId, MultipartFile file, AttachmentType type, Double duration) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }

        UploadDirectAttachmentCommand command = UploadDirectAttachmentCommand.builder()
                .uploaderId(userId)
                .inputStream(file.getInputStream())
                .contentType(file.getContentType())
                .sizeBytes(file.getSize())
                .extension(extension)
                .originalFilename(originalFilename)
                .type(type)
                .duration(duration)
                .build();

        Attachment attachment = uploadDirectAttachmentUseCase.uploadDirectAttachment(command);
        return presentationMediaMapper.toResponse(attachment);
    }
}
