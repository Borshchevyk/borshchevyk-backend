package ru.kubsu.borshchevyk.auth.config;

import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for HttpExchangeRepository.
 * Enables tracking of HTTP request/response metrics.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Configuration
public class HttpExchangeConfig {
    @Bean
    public HttpExchangeRepository httpExchangeRepository() {
        InMemoryHttpExchangeRepository repository = new InMemoryHttpExchangeRepository();
        repository.setCapacity(100);
        return repository;
    }
}