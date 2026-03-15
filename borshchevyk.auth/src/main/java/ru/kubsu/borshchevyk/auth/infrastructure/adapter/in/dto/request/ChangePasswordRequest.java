package ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String email;
    private String oldPassword;
    private String newPassword;
}
