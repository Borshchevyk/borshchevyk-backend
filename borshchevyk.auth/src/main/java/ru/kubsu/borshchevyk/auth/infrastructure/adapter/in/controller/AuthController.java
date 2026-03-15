package ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration, authentication, and password management")
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
        RegisterCommand registerCommand = registerMapper.toCommand(registerRequest);
        RegisterResult registerResult = registerUseCase.register(registerCommand);
        RegisterResponse registerResponse = registerMapper.toResponse(registerResult);
        return ResponseEntity.ok(registerResponse);
    }

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
        LoginCommand loginCommand = loginMapper.toCommand(loginRequest);
        LoginResult loginResult = loginUseCase.login(loginCommand);
        LoginResponse loginResponse = loginMapper.toResponse(loginResult);
        return ResponseEntity.ok(loginResponse);
    }

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
        ChallengeCommand challengeCommand = challengeMapper.toCommand(challengeRequest);
        ChallengeResult challengeResult = challengeUseCase.challenge(challengeCommand);
        ChallengeResponse challengeResponse = challengeMapper.toResponse(challengeResult);
        return ResponseEntity.ok(challengeResponse);
    }

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
        VerifyCommand verifyCommand = verifyMapper.toCommand(verifyRequest);
        VerifyResult verifyResult = verifyUseCase.verify(verifyCommand);
        VerifyResponse verifyResponse = verifyMapper.toResponse(verifyResult);
        return ResponseEntity.ok(verifyResponse);
    }

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
        ChangePasswordCommand changePasswordCommand = changePasswordMapper.toCommand(changePasswordRequest);
        changePasswordUseCase.changePassword(changePasswordCommand);
        return ResponseEntity.ok().build();
    }
}
