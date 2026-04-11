package ru.kubsu.borshchevyk.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;

/**
 * A gateway filter responsible for validating JSON Web Tokens (JWT) present in incoming requests.
 * Extracts the user ID from the JWT and propagates it via custom HTTP headers.
 * Denies access if the JWT is missing, improperly formatted, or invalid.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    /**
     * The secret key used for verifying JWT signatures.
     */
    private final SecretKey key;

    /**
     * Constructs a new JwtAuthenticationFilter with the specified JWT secret.
     *
     * @param secret the Base64-encoded JWT secret key loaded from application properties
     */
    public JwtAuthenticationFilter(@Value("${jwt.secret}") String secret) {
        super(Config.class);
        log.debug("Initializing JwtAuthenticationFilter with injected secret.");
        byte[] keyBytes = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Configuration class for the JwtAuthenticationFilter.
     */
    public static class Config { }

    /**
     * Applies the JWT authentication logic to the gateway filter chain.
     *
     * @param config the configuration for this filter
     * @return a GatewayFilter that enforces JWT validation
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                log.warn("Authorization header is missing from the request.");
                return onError(exchange, "Missing Authorization header");
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Authorization header has an invalid format (missing or not Bearer).");
                return onError(exchange, "Invalid Authorization header format");
            }

            String token = authHeader.substring(7);
            try {
                Claims claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                String userId = claims.getSubject();

                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header("X-User-Id", userId)
                        .build();

                log.debug("Authenticated request for user ID: {}", userId);
                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (Exception e) {
                log.warn("JWT Validation failed: {}", e.getMessage());
                return onError(exchange, "Invalid JWT token");
            }
        };
    }

    /**
     * Handles authentication errors by returning an HTTP 401 Unauthorized response.
     *
     * @param exchange the current server web exchange
     * @param err      the error message describing the failure cause
     * @return a Mono representing the completion of the response handling
     */
    private Mono<Void> onError(ServerWebExchange exchange, String err) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }
}
