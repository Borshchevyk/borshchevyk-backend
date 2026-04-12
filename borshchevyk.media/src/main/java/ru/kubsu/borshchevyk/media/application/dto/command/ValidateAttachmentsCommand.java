package ru.kubsu.borshchevyk.media.application.dto.command;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
@Builder
public class ValidateAttachmentsCommand {
    List<UUID> attachmentIds;
    UUID requesterId;
}
