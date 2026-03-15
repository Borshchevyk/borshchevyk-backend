package ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Helper component for JSON serialization.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JsonStringSerializer {

    private final ObjectMapper objectMapper;

    /**
     * Serializes an object to a JSON string.
     *
     * @param object the object to serialize
     * @return the JSON string
     * @throws RuntimeException if serialization fails
     */
    public String serialize(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object of type: {}", object.getClass().getName(), e);
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }
}
