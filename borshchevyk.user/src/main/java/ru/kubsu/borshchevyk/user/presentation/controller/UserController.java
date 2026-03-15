package ru.kubsu.borshchevyk.user.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.borshchevyk.user.application.dto.command.EditUserCommand;
import ru.kubsu.borshchevyk.user.application.port.in.DeleteUserUseCase;
import ru.kubsu.borshchevyk.user.application.port.in.EditUserUseCase;
import ru.kubsu.borshchevyk.user.domain.exception.*;
import ru.kubsu.borshchevyk.user.domain.model.result.EditUserResult;
import ru.kubsu.borshchevyk.user.presentation.dto.request.EditUserRequest;
import ru.kubsu.borshchevyk.user.presentation.dto.response.EditUserResponse;
import ru.kubsu.borshchevyk.user.presentation.mapper.UserMapper;

/**
 * Controller for managing user-related operations.
 * Provides endpoints for editing and deleting user profiles.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for managing user profiles and data")
public class UserController {

    private final EditUserUseCase editUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final UserMapper userMapper;

    /**
     * Updates user profile data.
     * Only the user themselves can update their profile.
     *
     * @param userId UUID of the user to update
     * @param requestUserId ID of the user from the request header (X-User-Id)
     * @param request Object containing fields to update
     * @return Updated user profile details
     * @throws UserForbiddenException if trying to update someone else's profile
     */
    @Operation(summary = "Edit user profile", description = "Partially updates user profile data such as email or tag.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile successfully updated",
                    content = @Content(schema = @Schema(implementation = EditUserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input format",
                    content = @Content(schema = @Schema(implementation = IncorrectInputFormatException.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden to edit other users",
                    content = @Content(schema = @Schema(implementation = UserForbiddenException.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = UserNotFoundException.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = UserServiceException.class)))
    })
    @PatchMapping("/{userId}")
    public ResponseEntity<EditUserResponse> editUser(
            @Parameter(description = "UUID of the user to edit", required = true)
            @PathVariable String userId,
            @Parameter(hidden = true)
            @RequestHeader("X-User-Id") String requestUserId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Fields to update", required = true)
            @RequestBody EditUserRequest request) {

        log.info("Received request to edit user with ID: {}", userId);
        
        if (!userId.equals(requestUserId)) {
            log.warn("Access denied: User {} tried to edit profile of user {}", requestUserId, userId);
            throw new ru.kubsu.borshchevyk.user.domain.exception.UserForbiddenException("You can only edit your own profile");
        }
        
        EditUserCommand command = userMapper.toCommand(userId, request);
        EditUserResult result = editUserUseCase.editUser(command);
        EditUserResponse response = userMapper.toResponse(result);
        
        log.info("Successfully updated user profile for ID: {}", userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a user profile.
     * Only the user themselves can delete their profile.
     *
     * @param userId UUID of the user to delete
     * @param requestUserId ID of the user from the request header (X-User-Id)
     * @return No content on success
     * @throws UserForbiddenException if trying to delete someone else's profile
     */
    @Operation(summary = "Delete user", description = "Permanently deletes a user from the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden to delete other users",
                    content = @Content(schema = @Schema(implementation = UserErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = UserNotFoundException.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = UserServiceException.class)))
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "UUID of the user to delete", required = true)
            @PathVariable String userId,
            @Parameter(hidden = true)
            @RequestHeader("X-User-Id") String requestUserId) {

        log.info("Received request to delete user with ID: {}", userId);
        
        if (!userId.equals(requestUserId)) {
            log.warn("Access denied: User {} tried to delete profile of user {}", requestUserId, userId);
            throw new ru.kubsu.borshchevyk.user.domain.exception.UserForbiddenException("You can only delete your own profile");
        }
        
        deleteUserUseCase.deleteUser(userId);
        log.info("Successfully deleted user with ID: {}", userId);
        return ResponseEntity.noContent().build();
    }
}
