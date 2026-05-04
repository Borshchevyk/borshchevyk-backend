package ru.kubsu.borshchevyk.calls;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * CallsApplication implementation.
 */
@SpringBootApplication
@Slf4j
public class CallsApplication {
    public static void main(String[] args) {
        log.info("Starting calls application...");
        SpringApplication.run(CallsApplication.class, args);
    }
}
