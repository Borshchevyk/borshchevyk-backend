package ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.borshchevyk.auth.application.dto.command.*;
import ru.kubsu.borshchevyk.auth.application.port.in.*;
import ru.kubsu.borshchevyk.auth.domain.exception.AuthServiceException;
import ru.kubsu.borshchevyk.auth.domain.exception.IncorrectInputFormatException;
import ru.kubsu.borshchevyk.auth.domain.exception.InvalidCredentialsException;
import ru.kubsu.borshchevyk.auth.domain.exception.UserAlreadyExistsException;
import ru.kubsu.borshchevyk.auth.domain.model.result.ChallengeResult;
import ru.kubsu.borshchevyk.auth.domain.model.result.LoginResult;
import ru.kubsu.borshchevyk.auth.domain.model.result.RegisterResult;
import ru.kubsu.borshchevyk.auth.domain.model.result.VerifyResult;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request.*;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.response.ChallengeResponse;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.response.LoginResponse;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.response.RegisterResponse;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.response.VerifyResponse;
import ru.kubsu.borshchevyk.auth.infrastructure.mapper.*;

/**
 * REST controller for authentication-related operations.
 *
 * @author Aleksey Timko
 * @since 2026-03-14
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration, authentication, and password management")
@Slf4j
public class AuthController {

    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;
    private final ChallengeUseCase challengeUseCase;
    private final VerifyUseCase verifyUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;

    private final RegisterMapper registerMapper;
    private final LoginMapper loginMapper;
    private final ChallengeMapper challengeMapper;
    private final VerifyMapper verifyMapper;
    private final ChangePasswordMapper changePasswordMapper;

    /**
     * Registers a new user.
     *
     * @param registerRequest the registration details
     * @return the registration response
     */
    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided email, tag, and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully registered",
                    content = @Content(schema = @Schema(implementation = RegisterResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input format (e.g. invalid email or tag)",
                    content = @Content(schema = @Schema(implementation = IncorrectInputFormatException.class))),
            @ApiResponse(responseCode = "409", description = "User with this email or tag already exists",
                    content = @Content(schema = @Schema(implementation = UserAlreadyExistsException.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AuthServiceException.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Registration details", required = true)
            @RequestBody RegisterRequest registerRequest) {
        log.info("Registering user with email: {}", registerRequest.email());
        RegisterCommand registerCommand = registerMapper.toCommand(registerRequest);
        RegisterResult registerResult = registerUseCase.register(registerCommand);
        RegisterResponse registerResponse = registerMapper.toResponse(registerResult);
        log.info("User registered successfully: {}", registerResponse.userId());
        return ResponseEntity.ok(registerResponse);
    }

    /**
     * Logins an existing user (Phase 1).
     *
     * @param loginRequest the login credentials
     * @return the login response (including public key for challenge)
     */
    @Operation(summary = "Login an existing user", description = "Authenticates a user and returns an access token if credentials are valid.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully logged in",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = InvalidCredentialsException.class))),
            @ApiResponse(responseCode = "400", description = "Bad request data",
                    content = @Content(schema = @Schema(implementation = AuthServiceException.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AuthServiceException.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Login credentials", required = true)
            @RequestBody LoginRequest loginRequest) {
        log.info("Login request for user: {}", loginRequest.email());
        LoginCommand loginCommand = loginMapper.toCommand(loginRequest);
        LoginResult loginResult = loginUseCase.login(loginCommand);
        LoginResponse loginResponse = loginMapper.toResponse(loginResult);
        log.info("Login phase 1 successful for user: {}", loginRequest.email());
        return ResponseEntity.ok(loginResponse);
    }

    /**
     * Requests a cryptographic challenge for authentication.
     *
     * @param challengeRequest the challenge request details
     * @return the generated challenge
     */
    @Operation(summary = "Request cryptographic challenge", description = "Generates a cryptographic challenge for client verification.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Challenge successfully generated",
                    content = @Content(schema = @Schema(implementation = ChallengeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(schema = @Schema(implementation = InvalidCredentialsException.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AuthServiceException.class)))
    })
    @PostMapping("/challenge")
    public ResponseEntity<ChallengeResponse> challenge(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Challenge request details", required = true)
            @RequestBody ChallengeRequest challengeRequest) {
        log.info("Challenge requested for user: {}", challengeRequest.userId());
        ChallengeCommand challengeCommand = challengeMapper.toCommand(challengeRequest);
        ChallengeResult challengeResult = challengeUseCase.challenge(challengeCommand);
        ChallengeResponse challengeResponse = challengeMapper.toResponse(challengeResult);
        log.info("Challenge generated for user: {}", challengeRequest.userId());
        return ResponseEntity.ok(challengeResponse);
    }

    /**
     * Verifies the cryptographic challenge signature.
     *
     * @param verifyRequest the verification details
     * @return the JWT tokens if verification is successful
     */
    @Operation(summary = "Verify cryptographic challenge", description = "Verifies the signature of the generated challenge to authenticate.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Challenge successfully verified",
                    content = @Content(schema = @Schema(implementation = VerifyResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid signature or challenge expired",
                    content = @Content(schema = @Schema(implementation = AuthServiceException.class))),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(schema = @Schema(implementation = AuthServiceException.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AuthServiceException.class)))
    })
    @PostMapping("/verify")
    public ResponseEntity<VerifyResponse> verify(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Verification details", required = true)
            @RequestBody VerifyRequest verifyRequest) {
        log.info("Verifying challenge for user: {}", verifyRequest.userId());
        VerifyCommand verifyCommand = verifyMapper.toCommand(verifyRequest);
        VerifyResult verifyResult = verifyUseCase.verify(verifyCommand);
        VerifyResponse verifyResponse = verifyMapper.toResponse(verifyResult);
        log.info("Challenge verified successfully for user: {}", verifyRequest.userId());
        return ResponseEntity.ok(verifyResponse);
    }

    /**
     * Changes the user's password.
     *
     * @param changePasswordRequest the password change details
     * @return 200 OK if successful
     */
    @Operation(summary = "Change user password", description = "Updates the user's password if the old password matches.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password successfully changed",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "401", description = "Invalid old password",
                    content = @Content(schema = @Schema(implementation = InvalidCredentialsException.class))),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(schema = @Schema(implementation = InvalidCredentialsException.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = AuthServiceException.class)))
    })
    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Password change details", required = true)
            @RequestBody ChangePasswordRequest changePasswordRequest) {
        log.info("Password change requested for user: {}", changePasswordRequest.getEmail());
        ChangePasswordCommand changePasswordCommand = changePasswordMapper.toCommand(changePasswordRequest);
        changePasswordUseCase.changePassword(changePasswordCommand);
        log.info("Password changed successfully for user: {}", changePasswordRequest.getEmail());
        return ResponseEntity.ok().build();
    }
}
