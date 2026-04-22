package ru.kubsu.borshchevyk.message.infrastructure.adapter.out.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.kubsu.borshchevyk.message.application.dto.response.AttachmentMetadataDto;
import ru.kubsu.borshchevyk.message.application.port.out.MediaPort;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class MediaAdapter implements MediaPort {

    private final RestTemplate restTemplate;

    @Value("${app.media-service.url:http://media-service:8086}")
    private String mediaServiceUrl;

    @Override
    public List<AttachmentMetadataDto> validateAttachments(List<UUID> attachmentIds, UUID userId) {
        if (attachmentIds == null || attachmentIds.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            String url = mediaServiceUrl + "/api/v1/media/validate";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-User-Id", userId.toString());
            headers.set("Content-Type", "application/json");

            ValidateAttachmentsRequest requestBody = new ValidateAttachmentsRequest(attachmentIds);
            HttpEntity<ValidateAttachmentsRequest> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<ValidateAttachmentsResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    ValidateAttachmentsResponse.class
            );

            if (response.getBody() != null && response.getBody().valid()) {
                return response.getBody().attachments() != null ? response.getBody().attachments() : new ArrayList<>();
            }
            return null; // Return null to indicate validation failure
        } catch (Exception e) {
            log.error("Failed to validate attachments via MediaService", e);
            return null; // Fail-safe: if we can't validate, we shouldn't allow sending
        }
    }

    record ValidateAttachmentsRequest(List<UUID> attachmentIds) {}
    record ValidateAttachmentsResponse(boolean valid, List<AttachmentMetadataDto> attachments) {}
}
