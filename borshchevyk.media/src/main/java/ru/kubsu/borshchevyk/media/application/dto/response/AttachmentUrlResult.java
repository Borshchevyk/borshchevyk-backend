package ru.kubsu.borshchevyk.media.application.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AttachmentUrlResult {
    String url;
}
