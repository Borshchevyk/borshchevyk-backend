package ru.kubsu.borshchevyk.user.domain.model.contact;

import lombok.Builder;
import lombok.Data;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class Contact {
    private UUID id;
    private UserId ownerId;
    private UserId contactUserId;
    private String contactFirstName;
    private String contactLastName;
    private LocalDateTime addedAt;
}
