package ru.kubsu.borshchevyk.user.domain.model.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class EditUserResult {
    private final String userId;
    private final String email;
    private final String tag;
}
