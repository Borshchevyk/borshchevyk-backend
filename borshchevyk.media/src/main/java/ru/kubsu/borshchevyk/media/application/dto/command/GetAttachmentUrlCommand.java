package ru.kubsu.borshchevyk.media.application.dto.command;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class GetAttachmentUrlCommand {
    UUID requesterId;
    UUID attachmentId;
}
