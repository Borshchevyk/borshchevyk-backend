package ru.kubsu.borshchevyk.media.application.dto.command;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CompleteUploadCommand {
    private UUID attachmentId;
    private UUID requesterId;
}
