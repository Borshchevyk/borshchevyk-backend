package ru.kubsu.borshchevyk.auth.application.port.out;

public interface PasswordEncoderPort {
    boolean matches(String rawPassword, String encodedPassword);
    String encode(String rawPassword);
}
