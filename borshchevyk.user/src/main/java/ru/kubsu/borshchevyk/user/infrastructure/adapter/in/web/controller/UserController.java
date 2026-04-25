package ru.kubsu.borshchevyk.user.infrastructure.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.borshchevyk.user.application.dto.command.GetUserProfileCommand;
import ru.kubsu.borshchevyk.user.application.dto.command.SearchUsersCommand;
import ru.kubsu.borshchevyk.user.application.dto.command.GetUsersBatchCommand;
import ru.kubsu.borshchevyk.user.application.port.in.GetUsersBatchUseCase;
import ru.kubsu.borshchevyk.user.application.port.in.GetUserProfileUseCase;
import ru.kubsu.borshchevyk.user.application.port.in.SearchUsersUseCase;
import ru.kubsu.borshchevyk.user.application.port.in.UpdatePrivacySettingsUseCase;
import ru.kubsu.borshchevyk.user.application.port.in.GetPrivacySettingsUseCase;
import ru.kubsu.borshchevyk.user.application.port.in.UpdateProfileUseCase;
import ru.kubsu.borshchevyk.user.domain.exception.UserErrorResponse;
import ru.kubsu.borshchevyk.user.infrastructure.adapter.in.web.dto.request.UpdatePrivacySettingsRequest;
import ru.kubsu.borshchevyk.user.infrastructure.adapter.in.web.dto.request.UpdateProfileRequest;
import ru.kubsu.borshchevyk.user.infrastructure.adapter.in.web.dto.response.PrivacySettingsResponse;
import ru.kubsu.borshchevyk.user.infrastructure.adapter.in.web.dto.response.UserProfileResponse;
import ru.kubsu.borshchevyk.user.infrastructure.adapter.in.web.mapper.PresentationUserMapper;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "Operations related to users and privacy")
public class UserController {

    private final SearchUsersUseCase searchUsersUseCase;
    private final GetUserProfileUseCase getUserProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final UpdatePrivacySettingsUseCase updatePrivacySettingsUseCase;
    private final GetPrivacySettingsUseCase getPrivacySettingsUseCase;
    private final GetUsersBatchUseCase getUsersBatchUseCase;
    private final PresentationUserMapper mapper;

    @Operation(summary = "Get users in batch", description = "Gets multiple profiles by userIds")
    @ApiResponse(responseCode = "200", description = "Users found")
    @PostMapping("/batch")
    public ResponseEntity<List<UserProfileResponse>> getUsersBatch(
            @RequestBody List<UUID> userIds,
            @RequestHeader(value = "X-User-Id", required = false) String requesterId) {
        var command = GetUsersBatchCommand.builder()
                .userIds(userIds)
                .requesterId(requesterId)
                .build();
        var users = getUsersBatchUseCase.getUsersBatch(command);
        return ResponseEntity.ok(users.stream()
                .map(mapper::toUserProfileResponse)
                .toList());
    }

    @Operation(summary = "Get user profile", description = "Gets profile by userId or tag")
    @ApiResponse(responseCode = "200", description = "Profile found")
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = UserErrorResponse.class)))
    @GetMapping("/{userIdOrTag}")
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @PathVariable String userIdOrTag,
            @RequestHeader(value = "X-User-Id", required = false) String requesterId) {
        var command = GetUserProfileCommand.builder()
                .targetUserIdOrTag(userIdOrTag)
                .requesterId(requesterId)
                .build();
        var user = getUserProfileUseCase.getUserProfile(command);
        return ResponseEntity.ok(mapper.toUserProfileResponse(user));
    }

    @Operation(summary = "Update my profile", description = "Updates profile for the current user")
    @ApiResponse(responseCode = "200", description = "Profile updated")
    @PatchMapping("/me/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody @Valid UpdateProfileRequest request) {
        var command = mapper.toUpdateProfileCommand(request, userId);
        var updatedUser = updateProfileUseCase.updateProfile(command);
        return ResponseEntity.ok(mapper.toUserProfileResponse(updatedUser));
    }

    @Operation(summary = "Set main avatar", description = "Sets the main avatar from previously uploaded ones or newly uploaded.")
    @ApiResponse(responseCode = "200", description = "Avatar set successfully")
    @PutMapping("/me/avatar")
    public ResponseEntity<UserProfileResponse> setAvatar(
            @RequestBody ru.kubsu.borshchevyk.user.infrastructure.adapter.in.web.dto.request.SetAvatarRequest request,
            @RequestHeader("X-User-Id") String userId) {
        
        ru.kubsu.borshchevyk.user.application.dto.command.UpdateProfileCommand command = ru.kubsu.borshchevyk.user.application.dto.command.UpdateProfileCommand.builder()
                .userId(userId)
                .avatarUrl(request.avatarUrl())
                .build();
                
        var updatedUser = updateProfileUseCase.updateProfile(command);
        return ResponseEntity.ok(mapper.toUserProfileResponse(updatedUser));
    }

    @Operation(summary = "Get my privacy settings")
    @ApiResponse(responseCode = "200", description = "Successful retrieval")
    @GetMapping("/me/privacy")
    public ResponseEntity<PrivacySettingsResponse> getPrivacySettings(
            @RequestHeader("X-User-Id") String userId) {
        var settings = getPrivacySettingsUseCase.getPrivacySettings(userId);
        return ResponseEntity.ok(mapper.toPrivacySettingsResponse(settings));
    }

    @Operation(summary = "Update my privacy settings")
    @ApiResponse(responseCode = "200", description = "Settings updated")
    @PatchMapping("/me/privacy")
    public ResponseEntity<PrivacySettingsResponse> updatePrivacySettings(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody @Valid UpdatePrivacySettingsRequest request) {
        var command = mapper.toUpdatePrivacySettingsCommand(request, userId);
        var updatedSettings = updatePrivacySettingsUseCase.updatePrivacySettings(command);
        return ResponseEntity.ok(mapper.toPrivacySettingsResponse(updatedSettings));
    }
}
