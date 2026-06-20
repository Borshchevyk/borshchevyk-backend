package ru.kubsu.borshchevyk.message.domain.model.chat.member_permissions;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class ChatMemberPermissions {

    private final Set<PermissionType> permissions = new HashSet<>(Set.of(PermissionType.values()));

    public void addPermission(PermissionType permission) {
        permissions.add(permission);
    }

    public void revokePermission(PermissionType permission) {
        permissions.remove(permission);
    }

    public boolean hasPermission(PermissionType permission) {
        return this.permissions.contains(permission);
    }

    public boolean hasPermissions(Set<PermissionType> permissions) {
        return this.permissions.containsAll(permissions);
    }

    public enum PermissionType {
        SEND_MESSAGES,
        DELETE_MESSAGES,
        INVITE_USERS,
        CHANGE_CHAT_INFO;
    }
}
