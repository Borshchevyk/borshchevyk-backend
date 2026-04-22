package ru.kubsu.borshchevyk.message.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentMetadataDto {
    private UUID id;
    private String type;
}
