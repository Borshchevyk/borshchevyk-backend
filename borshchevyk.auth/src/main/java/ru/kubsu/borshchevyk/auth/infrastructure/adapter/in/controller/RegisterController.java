package ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kubsu.borshchevyk.auth.application.dto.command.RegisterCommand;
import ru.kubsu.borshchevyk.auth.application.port.in.RegisterUseCase;
import ru.kubsu.borshchevyk.auth.domain.model.result.RegisterResult;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request.RegisterRequest;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.response.RegisterResponse;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.mapper.RegisterMapper;

/**
 * REST controller for user registration.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration, authentication, and password management")
@Slf4j
public class RegisterController {

    private final RegisterUseCase registerUseCase;
    private final RegisterMapper registerMapper;

    /**
     * Registers a new user.
     *
     * @param registerRequest the registration details
     * @return the registration response
     */
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with the provided email, tag, and password."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully registered",
                    content = @Content(schema = @Schema(implementation = RegisterResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input format (e.g. invalid email or tag)",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "User with this email or tag already exists",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest registerRequest
    ) {
        log.info("Registering user"); // Removed PII (email)
        RegisterCommand registerCommand = registerMapper.toCommand(registerRequest);
        RegisterResult registerResult = registerUseCase.register(registerCommand);
        RegisterResponse registerResponse = registerMapper.toResponse(registerResult);
        log.info("User registered successfully: {}", registerResponse.userId());
        return ResponseEntity.ok(registerResponse);
    }
}
