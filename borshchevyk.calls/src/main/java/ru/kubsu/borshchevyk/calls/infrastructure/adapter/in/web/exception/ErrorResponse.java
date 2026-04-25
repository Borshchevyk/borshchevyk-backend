package ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Standard error response DTO.
 *
 * @author Aleksey Timko
 * @since 2026-04-25
 */
@Data
@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;
}
