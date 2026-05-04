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
import ru.kubsu.borshchevyk.auth.application.dto.command.LoginCommand;
import ru.kubsu.borshchevyk.auth.application.port.in.LoginUseCase;
import ru.kubsu.borshchevyk.auth.domain.model.result.LoginResult;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request.LoginRequest;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.response.LoginResponse;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.mapper.LoginMapper;

/**
 * REST controller for logging in.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
@Slf4j
public class LoginController {

    private final LoginUseCase loginUseCase;
    private final LoginMapper loginMapper;

    /**
     * Logins an existing user (Phase 1).
     *
     * @param loginRequest the login credentials
     * @return the login response (including public key for challenge)
     */
    @Operation(
            summary = "Login an existing user",
            description = "Authenticates a user and returns an access token if credentials are valid."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully logged in",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "400", description = "Bad request data",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        log.info("Login request received");
        LoginCommand loginCommand = loginMapper.toCommand(loginRequest);
        LoginResult loginResult = loginUseCase.login(loginCommand);
        LoginResponse loginResponse = loginMapper.toResponse(loginResult);
        log.info("Login phase 1 successful");
        return ResponseEntity.ok(loginResponse);
    }
}
