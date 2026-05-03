package ru.kubsu.borshchevyk.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Auth microservice.
 */
@Slf4j
@SpringBootApplication
public class Application {

    /**
     * Main method to start the Spring Boot application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        log.info("Starting Auth application...");
        SpringApplication.run(Application.class, args);
    }

}
