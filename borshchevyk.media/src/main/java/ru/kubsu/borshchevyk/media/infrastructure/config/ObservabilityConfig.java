package ru.kubsu.borshchevyk.media.infrastructure.config;

import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for observability and actuator endpoints.
 * Enables HTTP exchange tracking for Spring Boot Admin.
 *
 * @author Aleksey Timko
 */
@Configuration
public class ObservabilityConfig {

    @Bean
    public HttpExchangeRepository httpExchangeRepository() {
        return new InMemoryHttpExchangeRepository();
    }
}
