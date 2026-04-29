package ru.kubsu.borshchevyk.gateway.config;

import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for HTTP request/response metrics repository.
 * Exposes all HTTP exchanges to the Spring Boot Admin Panel.
 *
 * @author Aleksey Timko
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