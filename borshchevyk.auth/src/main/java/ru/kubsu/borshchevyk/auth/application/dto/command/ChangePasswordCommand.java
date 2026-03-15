package ru.kubsu.borshchevyk.auth.application.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ChangePasswordCommand {
    private final String email;
    private final String oldPassword;
    private final String newPassword;
}
