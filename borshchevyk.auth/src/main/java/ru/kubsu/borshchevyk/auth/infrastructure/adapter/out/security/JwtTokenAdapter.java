package ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.auth.application.port.out.TokenGeneratorPort;
import ru.kubsu.borshchevyk.auth.domain.model.account.Account;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtTokenAdapter implements TokenGeneratorPort {

    private final SecretKey secretKey;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    public JwtTokenAdapter(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiration-ms}") long accessTokenExpirationMs,
            @Value("${jwt.refresh-expiration-ms}") long refreshTokenExpirationMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    @Override
    public String generateAccessToken(Account account) {
        return Jwts.builder()
                .subject(account.getAccountId().value().toString())
                .claim("email", account.getEmail().getValue())
                .claim("tag", account.getTag().getValue())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(accessTokenExpirationMs, ChronoUnit.MILLIS)))
                .signWith(secretKey)
                .compact();
    }

    @Override
    public String generateRefreshToken(Account account) {
        return Jwts.builder()
                .subject(account.getAccountId().value().toString())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(refreshTokenExpirationMs, ChronoUnit.MILLIS)))
                .signWith(secretKey)
                .compact();
    }
}
