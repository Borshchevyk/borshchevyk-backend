package ru.kubsu.borshchevyk.media.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kubsu.borshchevyk.media.application.port.out.AttachmentPort;
import ru.kubsu.borshchevyk.media.application.port.out.S3Port;
import ru.kubsu.borshchevyk.media.domain.model.Attachment;
import ru.kubsu.borshchevyk.media.domain.model.AttachmentStatus;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CleanupService {

    private final AttachmentPort attachmentPort;
    private final S3Port s3Port;

    @Scheduled(cron = "0 0 * * * *") // Run every hour
    @Transactional
    public void cleanupAbandonedUploads() {
        log.info("Starting cleanup of abandoned attachments");
        
        // Find attachments stuck in UPLOADING state for more than 2 hours
        LocalDateTime threshold = LocalDateTime.now().minusHours(2);
        List<Attachment> abandonedAttachments = attachmentPort.findByStatusAndCreatedAtBefore(AttachmentStatus.UPLOADING, threshold);
        
        for (Attachment attachment : abandonedAttachments) {
            log.info("Deleting abandoned attachment: {}", attachment.getId().value());
            try {
                s3Port.deleteObject(attachment.getS3Key());
                attachmentPort.delete(attachment.getId());
            } catch (Exception e) {
                log.error("Failed to delete abandoned attachment: {}", attachment.getId().value(), e);
            }
        }
        
        log.info("Finished cleanup. Deleted {} abandoned attachments", abandonedAttachments.size());
    }
}
