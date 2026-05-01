package ru.kubsu.borshchevyk.message;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Borshchevyk Message Service.
 * Bootstraps the Spring context.
 *
 * @author Aleksey Timko
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
