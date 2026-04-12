package ru.kubsu.borshchevyk.message.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PresenceStatusResponse {
    private UUID userId;
    private boolean isOnline;
    private Long lastSeenAt;
}
