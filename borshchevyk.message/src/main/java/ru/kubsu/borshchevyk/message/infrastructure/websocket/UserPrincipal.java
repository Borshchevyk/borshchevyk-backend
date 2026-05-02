package ru.kubsu.borshchevyk.message.infrastructure.websocket;

import java.security.Principal;
import java.util.Objects;

/**
 * @author Aleksey Timko
 */
public class UserPrincipal implements Principal {
    private final String userId;

    public UserPrincipal(String userId) {
        this.userId = userId;
    }

    @Override
    public String getName() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrincipal that = (UserPrincipal) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}