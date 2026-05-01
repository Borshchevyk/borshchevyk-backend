package ru.kubsu.borshchevyk.message.domain.model.chat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Domain model representing a Private chat type.
 *
 * @author Aleksey Timko
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class PrivateChat extends Chat {
}
