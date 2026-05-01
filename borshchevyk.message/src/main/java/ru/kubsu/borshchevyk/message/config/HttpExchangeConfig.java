package ru.kubsu.borshchevyk.message.config;

import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for HttpExchangeRepository.
 * Enables exposing HTTP request/response metrics to Spring Boot Admin.
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