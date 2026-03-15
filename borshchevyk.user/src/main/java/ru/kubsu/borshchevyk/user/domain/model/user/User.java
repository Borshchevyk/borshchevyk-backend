package ru.kubsu.borshchevyk.user.domain.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.kubsu.borshchevyk.user.domain.model.value.Email;
import ru.kubsu.borshchevyk.user.domain.model.value.Tag;
import ru.kubsu.borshchevyk.user.domain.model.value.UserId;

@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @EqualsAndHashCode.Include
    private final UserId userId;

    private Email email;
    private Tag tag;
}
