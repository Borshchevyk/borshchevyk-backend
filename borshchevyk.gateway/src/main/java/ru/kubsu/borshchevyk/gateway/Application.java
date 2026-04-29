package ru.kubsu.borshchevyk.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.autoconfigure.LifecycleMvcEndpointAutoConfiguration;

/**
 * Main entry point for the API Gateway application.
 * Bootstraps the Spring Boot application and configures necessary components.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Slf4j
@SpringBootApplication(exclude = {LifecycleMvcEndpointAutoConfiguration.class})
public class Application {

    /**
     * Application execution entry point.
     *
     * @param args command line arguments passed to the application
     */
    public static void main(String[] args) {
        log.info("Starting API Gateway application...");
        SpringApplication.run(Application.class, args);
        log.info("API Gateway application started successfully.");
    }

}
