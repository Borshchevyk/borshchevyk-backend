package ru.kubsu.borshchevyk.admin;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Borshchevyk Admin Service.
 *
 * @author Aleksey Timko
 */
@SpringBootApplication
@EnableAdminServer
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
