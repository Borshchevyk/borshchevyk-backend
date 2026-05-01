package ru.kubsu.borshchevyk.message.domain.model.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.Builder;

/**
 * Domain model representing a Group chat type.
 *
 * @author Aleksey Timko
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GroupChat extends Chat {
    private String title;
    private String description;
    private String inviteCode;
    @Builder.Default
    private boolean commentsEnabled = true;
}
